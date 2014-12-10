package org.yarpc.common.server;

import org.yarpc.common.protocol.BaseResponse;

import java.net.SocketAddress;

/**
 * Created by jingtian.zjt on 2014/12/3.
 */
public interface Connection {

    public SocketAddress getLocalAddress();

    public SocketAddress getRemoteAddress();

    public void write(BaseResponse response);

}
