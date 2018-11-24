package com.icinfo.clink.core;

import java.io.IOException;

/**
 * 描述:  <br>
 *
 * @author jkk
 * @date 2018年11月24
 */
public interface Receiver {
    boolean receiveAsync(IoArgs.IoArgsEventListener listener) throws IOException;
}
