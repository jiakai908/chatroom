package com.icinfo.clink.core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * 描述:  <br>
 *
 * @author jkk
 * @date 2018年11月24
 */
public class IoArgs {
    private byte[] byteBuffer = new byte[256];
    private ByteBuffer buffer = ByteBuffer.wrap(byteBuffer);

    public int read(SocketChannel channel) throws IOException {
        buffer.clear();
        return channel.read(buffer);
    }

    public int write(SocketChannel channel) throws IOException {
        return channel.write(buffer);
    }

    public String bufferString(){
        // 丢弃换行符
        return new String(byteBuffer,0,buffer.position()-1);
    }

    public interface IoArgsEventListener{
        void onStarted(IoArgs ioArgs);
        void onComplete(IoArgs ioArgs);
    }

}
