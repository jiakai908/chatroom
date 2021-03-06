package com.icinfo.clink.core;

import java.io.Closeable;
import java.nio.channels.SocketChannel;

/**
 * 描述:  <br>
 *
 * @author jkk
 * @date 2018年11月24
 */
public interface IoProvider extends Closeable {
    boolean registerInput(SocketChannel channel, HandleInputCallback callback);

    boolean registerOutput(SocketChannel channel, HandleOutputCallback callback);

    void unRegisterInput(SocketChannel channel);
    void unRegisterOutput(SocketChannel channel);

    abstract class HandleInputCallback implements Runnable {
        public void run() {
            canProviderInput();
        }

        protected abstract void canProviderInput();
    }

    abstract class HandleOutputCallback implements Runnable {
        private Object attach;

        public void run() {
            canProviderOutput(attach);
        }

        public final void setAttach(Object attach) {
            this.attach = attach;
        }

        protected abstract void canProviderOutput(Object attach);
    }
}
