package org.yarpc.netty.codecs;

import org.yarpc.netty.buffer.NettyByteBufferWrapper;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.yarpc.common.protocol.BaseHeader;

/**
 * Created by jingtian.zjt on 2014/12/3.
 */
public class NettyProtocolEncoder extends OneToOneEncoder {

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        NettyByteBufferWrapper wrapper = new NettyByteBufferWrapper();
        ((BaseHeader)msg).encode(wrapper);
        return wrapper.getBuffer();
    }
}
