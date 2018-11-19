package com.icinfo.sample.client;

import com.icinfo.sample.client.bean.ServerInfo;

public class Client {
    public static void main(String[] args) {
        ServerInfo serverInfo = UDPSearcher.searchServer(10000);
        System.out.println("Server:"+serverInfo);
        if (serverInfo!=null){
            try {
                TCPClient.linkWith(serverInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
