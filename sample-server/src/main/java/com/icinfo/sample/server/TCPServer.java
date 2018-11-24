package com.icinfo.sample.server;

import com.icinfo.clink.utils.CloseUtils;
import com.icinfo.sample.server.handle.ClientHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 描述:  <br>
 *
 * @author jkk
 * @date 2018年11月16
 */
public class TCPServer implements ClientHandler.ClientHandlerCallback {
    private final int port;
    private ClientListener mListener;
    private List<ClientHandler> clientHandlerList = new ArrayList<ClientHandler>();
    private final ExecutorService forwardingThreadPoolExecutor;
    private Selector selector;
    private ServerSocketChannel server;

    public TCPServer(int port) {
        this.port = port;
        // 转发线程池
        this.forwardingThreadPoolExecutor = Executors.newSingleThreadExecutor();
    }

    public Boolean start() {
        try {
            selector = Selector.open();

            ServerSocketChannel server = ServerSocketChannel.open();
            //设置非阻塞
            server.configureBlocking(false);

            server.socket().bind(new InetSocketAddress(port));
            //注册监听客户端到达
            server.register(selector, SelectionKey.OP_ACCEPT);

            this.server = server;

            System.out.println("服务器信息：" + server.getLocalAddress().toString());


            ClientListener listener = this.mListener = new ClientListener();
            listener.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void stop() {
        if (mListener != null) {
            mListener.exit();
        }
        CloseUtils.close(server);
        CloseUtils.close(selector);

        synchronized (TCPServer.this) {
            for (ClientHandler clientHandler : clientHandlerList) {
                clientHandler.exit();
            }
            clientHandlerList.clear();
        }
        forwardingThreadPoolExecutor.shutdown();
    }

    public synchronized void broadcast(String str) {
        for (ClientHandler clientHandler : clientHandlerList) {
            clientHandler.send(str);
        }
    }

    @Override
    public synchronized void onSelfClosed(ClientHandler clientHandler) {
        clientHandlerList.remove(clientHandler);
    }

    @Override
    public void onMessageArried(ClientHandler clientHandler, String msg) {
        System.out.println("Received-" + clientHandler.getClientInfo() + ":" + msg);
        forwardingThreadPoolExecutor.execute(() -> {
            synchronized (TCPServer.this) {
                for (ClientHandler handler : clientHandlerList) {
                    if (handler.equals(clientHandler)) {
                        continue;
                    }
                    // 对其他客户端发送消息
                    handler.send(msg);
                }
            }
        });

    }

    private class ClientListener extends Thread {
        private boolean done = false;

        @Override
        public void run() {
            super.run();
            Selector selector = TCPServer.this.selector;

            System.out.println("服务器准备就绪～");
            // 等待客户端连接
            do {
                try {
                    if (selector.select() == 0) {
                        if (done) {
                            return;
                        }
                    }
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        if (done) {
                            break;
                        }
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        //检查客户端到达状态
                        if (key.isAcceptable()) {
                            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                            //非阻塞获取客户端
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            try {
                                // 客户端构建异步线程
                                ClientHandler clientHandler = new ClientHandler(socketChannel, TCPServer.this);
                                // 读取并打印数据
                                clientHandler.readToPrint();
                                synchronized (TCPServer.this) {
                                    clientHandlerList.add(clientHandler);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                System.out.println("客户端连接异常：" + e.getMessage());
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } while (!done);

        }

        void exit() {
            done = true;
            //唤醒当前阻塞
            selector.wakeup();
        }
    }
}
