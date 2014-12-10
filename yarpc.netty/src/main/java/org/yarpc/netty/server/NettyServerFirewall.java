package org.yarpc.netty.server;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.ipfilter.IpFilteringHandlerImpl;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by jingtian.zjt on 2014/12/3.
 */
public class NettyServerFirewall extends IpFilteringHandlerImpl{

    private final AtomicBoolean refused = new AtomicBoolean(false);
    @Override
    protected boolean accept(ChannelHandlerContext ctx, ChannelEvent e, InetSocketAddress inetSocketAddress) throws Exception {
        return !refused.get();
    }
    public void setRefused(boolean refused) {
        this.refused.set(refused);
    }
}
