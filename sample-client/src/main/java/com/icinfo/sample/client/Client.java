package com.icinfo.sample.client;

import com.icinfo.sample.client.bean.ServerInfo;

import java.io.*;

public class Client {
    public static void main(String[] args) {
        ServerInfo serverInfo = UDPSearcher.searchServer(100000);
        TCPClient tcpClient = null;
        System.out.println("Server:"+serverInfo);
        if (serverInfo!=null){
            try {
                tcpClient = TCPClient.startWith(serverInfo);
                if (tcpClient == null){
                    return;
                }
                write(tcpClient);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                tcpClient.exit();
            }
        }
    }

    private static void write(TCPClient tcpClient) throws IOException {
            InputStream in = System.in;
            BufferedReader input = new BufferedReader(new InputStreamReader(in));
            do {
                String str = input.readLine();
                tcpClient.send(str);
                if ("00bye00".equalsIgnoreCase(str)){
                    break;
                }
            }while (true);
    }
}
