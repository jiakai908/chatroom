package com.icinfo.sample.server;

import com.icinfo.sample.server.handle.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 描述:  <br>
 *
 * @author jkk
 * @date 2018年11月16
 */
public class TCPServer {
    private final int port;
    private ClientListener mListener;
    private List<ClientHandler> clientHandlerList = new ArrayList<ClientHandler>();

    public TCPServer(int port) {
        this.port = port;
    }

    public Boolean start() {
        try {
            ClientListener listener = new ClientListener(port);
            mListener = listener;
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
        for (ClientHandler clientHandler : clientHandlerList) {
            clientHandler.exit();
        }
    }

    public void broadcast(String str) {

        for (ClientHandler clientHandler : clientHandlerList) {
            clientHandler.send(str);
        }
    }

    private class ClientListener extends Thread {
        private ServerSocket server;
        private boolean done = false;

        private ClientListener(int port) throws IOException {
            server = new ServerSocket(port);
            System.out.println("服务器信息：" + server.getInetAddress() + " P:" + server.getLocalPort());

        }

        @Override
        public void run() {
            super.run();
            System.out.println("服务器准备就绪～");
            // 等待客户端连接
            do {
                Socket client;
                try {
                    client = server.accept();
                } catch (IOException e) {
                    continue;
                }


            } while (!done);
        }

        void exit() {
            done = true;
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}