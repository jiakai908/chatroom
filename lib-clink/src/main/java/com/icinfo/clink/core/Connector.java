package com.icinfo.clink.core;

import java.nio.channels.SocketChannel;
import java.util.UUID;

/**
 * 描述:  <br>
 *
 * @author jkk
 * @date 2018年11月24
 */
public class Connector {
    private UUID key = UUID.randomUUID();
    private SocketChannel channel;
    private Sender sender;
    private Receiver receiver;

    public void setup(SocketChannel channel) {
        this.channel = channel;
    }
}
