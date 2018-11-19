package com.icinfo.sample.client;

import com.icinfo.clink.utils.CloseUtils;
import com.icinfo.sample.client.bean.ServerInfo;

import java.io.*;
import java.net.*;

public class TCPClient {

    public static void linkWith(ServerInfo serverInfo) throws IOException {
        Socket socket = new Socket();
        socket.setSoTimeout(3000);

        socket.connect(new InetSocketAddress(Inet4Address.getByName(serverInfo.getAddress()), serverInfo.getPort()), 3000);
        System.out.println("已发起服务器连接，并进入后续流程～");
        System.out.println("客户端信息：" + socket.getLocalAddress() + " P:" + socket.getLocalPort());
        System.out.println("服务器信息：" + socket.getInetAddress() + " P:" + socket.getPort());

        try {
            ReadHandler readHandler = new ReadHandler(socket.getInputStream());
            readHandler.start();

            //接收数据
            write(socket);

            //退出操作
            readHandler.exit();
        } catch (IOException e) {
            System.out.println("异常关闭");
        } finally {
            socket.close();
            System.out.println("客户端已退出～");
        }

    }

    private static void write(Socket client) throws IOException {
        InputStream in = System.in;
        BufferedReader input = new BufferedReader(new InputStreamReader(in));

        //得到socket输出流
        OutputStream outputStream = client.getOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        do {
            String str = input.readLine();
            printStream.println(str);

            if ("00bye00".equalsIgnoreCase(str)){
                break;
            }
        }while (true);

        printStream.close();
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
