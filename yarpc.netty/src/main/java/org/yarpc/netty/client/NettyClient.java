package org.yarpc.netty.client;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.yarpc.common.client.BaseClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yarpc.common.protocol.BaseRequest;
import org.yarpc.common.protocol.BaseResponse;
import org.yarpc.common.protocol.ProtocolStatus;

/**
 * Created by jingtian.zjt on 2014/12/7.
 */
public class NettyClient extends BaseClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);
    private Channel channel;
    public NettyClient(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void send(final BaseRequest request) {
        ChannelFuture future = channel.write(request);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    return;
                } else {
                    channel.close();
                    LOGGER.error("[RPC] send to " + channel.getRemoteAddress() + " failed.", future.getCause());
                    BaseResponse response = request.createErrorResponse(ProtocolStatus.ERROR, "send failed.");
                    complete(response);
                }
            }
        });
    }

    @Override
    public boolean isConnected() {
        return channel.isConnected();
    }
}
