package org.yarpc.netty.server;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.yarpc.common.server.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yarpc.common.protocol.BaseResponse;

import java.net.SocketAddress;

/**
 * Created by jingtian.zjt on 2014/12/3.
 */
public class NettyConnection implements Connection {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyConnection.class);
    private final Channel channel;
    public NettyConnection(final Channel channel) {
        this.channel = channel;
    }
    @Override
    public SocketAddress getLocalAddress() {
        return channel.getRemoteAddress();
    }
    @Override
    public SocketAddress getRemoteAddress() {
        return channel.getLocalAddress();
    }

    @Override
    public void write(BaseResponse response) {
        ChannelFuture future = channel.write(response);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    LOGGER.error("[REMOTE] write to " + future.getChannel().getRemoteAddress() + " failed");
                }
            }
        });
    }
}
