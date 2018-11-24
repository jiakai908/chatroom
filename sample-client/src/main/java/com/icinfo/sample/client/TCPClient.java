package com.icinfo.sample.client;

import com.icinfo.clink.utils.CloseUtils;
import com.icinfo.sample.client.bean.ServerInfo;

import java.io.*;
import java.net.*;

public class TCPClient {
    private final Socket socket;
    private final ReadHandler readHandler;
    private final PrintStream printStream;

    public TCPClient(Socket socket, ReadHandler readHandler) throws IOException {
        this.socket = socket;
        this.readHandler = readHandler;
        this.printStream = new PrintStream(socket.getOutputStream());
    }

    public void exit(){
        readHandler.exit();
        CloseUtils.close(printStream);
        CloseUtils.close(socket);
    }

    public void send(String msg){
        printStream.println(msg);
    }

    public static TCPClient startWith(ServerInfo serverInfo) throws IOException {
        Socket socket = new Socket();
        socket.setSoTimeout(3000);

        socket.connect(new InetSocketAddress(Inet4Address.getByName(serverInfo.getAddress()), serverInfo.getPort()), 3000);
        System.out.println("已发起服务器连接，并进入后续流程～");
        System.out.println("客户端信息：" + socket.getLocalAddress() + " P:" + socket.getLocalPort());
        System.out.println("服务器信息：" + socket.getInetAddress() + " P:" + socket.getPort());

        try {
            ReadHandler readHandler = new ReadHandler(socket.getInputStream());
            readHandler.start();
            return new TCPClient(socket,readHandler);
        } catch (IOException e) {
            System.out.println("连接异常");
            CloseUtils.close(socket);
        }
        return null;
    }



    private static class ReadHandler extends Thread {
        private boolean done = false;
        private final InputStream inputStream;

        public ReadHandler(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            super.run();
            // 得到输入流，用于接收数据
            try {
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(inputStream));

                do {
                    String str;
                    try {
                        str = socketInput.readLine();
                    } catch (IOException e) {
                        continue;
                    }
                    if (str == null) {
                        System.out.println("连接已关闭，无法读取数据！");
                        break;
                    }
                    System.out.println(str);
                } while (!done);
            } catch (Exception e) {
                if (!done) {
                    System.out.println("连接异常断开：" + e.getMessage());
                }
            } finally {
                CloseUtils.close(inputStream);
            }
        }

        void exit(){
            done = true;
            CloseUtils.close(inputStream);
        }
    }
}
