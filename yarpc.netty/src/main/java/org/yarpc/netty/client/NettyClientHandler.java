package org.yarpc.netty.client;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yarpc.common.protocol.BaseResponse;

import java.net.ConnectException;
/**
 * Created by jingtian.zjt on 2014/12/7.
 */
public class NettyClientHandler extends IdleStateHandler{
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClientHandler.class);

    private NettyClientFactory factory;

    public NettyClientHandler(NettyClientFactory factory, Timer timer, int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds) {
        super(timer, readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds);
        this.factory = factory;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof BaseResponse) {
            BaseResponse response = (BaseResponse) e.getMessage();
            NettyClient client = (NettyClient) factory.get(ctx.getChannel().getRemoteAddress());
            client.complete(response);
        } else {
            LOGGER.error("[REMOTE] unsupported message type from " + ctx.getChannel().getRemoteAddress());
            throw new Exception("unsupported message type");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception{
        if (e.getCause() instanceof ConnectException) {
            return ;
        }
        LOGGER.error("[REMOTE] catch exception:", e.getCause());
    }
}
