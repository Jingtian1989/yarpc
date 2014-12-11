package org.yarpc.netty.client;

import org.yarpc.common.client.BaseClientFactory;
import org.yarpc.common.client.Client;
import org.yarpc.common.exception.YarpcCode;
import org.yarpc.common.exception.YarpcException;
import org.yarpc.netty.codecs.NettyProtocolEncoder;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.util.HashedWheelTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yarpc.common.protocol.ProtocolSetting;
import org.yarpc.common.thread.ThreadPoolManager;
import org.yarpc.netty.codecs.NettyProtocolDecoder;

import java.net.SocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by jingtian.zjt on 2014/12/7.
 */
public class NettyClientFactory extends BaseClientFactory {

    private final static Logger LOGGER = LoggerFactory.getLogger(NettyClientFactory.class);
    private final ClientBootstrap bootstrap;


    public NettyClientFactory() {
        ThreadFactory master = new ThreadPoolManager.NamedThreadFactory("[YARPC-CLIENT-MASTER]");
        ThreadFactory worker = new ThreadPoolManager.NamedThreadFactory("[YARPC-CLIENT-WORKER]");
        bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(master),
                Executors.newCachedThreadPool(worker)));
        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("reuseAddress", true);
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = new DefaultChannelPipeline();
                pipeline.addLast("decoder", new NettyProtocolDecoder());
                pipeline.addLast("encoder", new NettyProtocolEncoder());
                pipeline.addLast("handler", new NettyClientHandler(NettyClientFactory.this, new HashedWheelTimer(), ProtocolSetting.DEFAULT_MAX_IDLE, 0, 0));
                return pipeline;
            }
        });
    }

    @Override
    public Client connect(SocketAddress address, int timeout) throws YarpcException {
        ChannelFuture future = bootstrap.connect(address);
        future.awaitUninterruptibly(timeout);
        if (!future.isDone()) {
            LOGGER.error("[YARPC] connect " + address + " timeout.");
            throw new YarpcException(YarpcCode.YARPC_CLIENT_CONN_TIMEOUT, "connect remote timeout.", future.getCause());
        }
        if (!future.isSuccess()) {
            LOGGER.error("[YARPC] connect " + address + " failed.");
            throw new YarpcException(YarpcCode.YARPC_CLIENT_CONN_FAILED, "connect failed.", future.getCause());
        }

        if (!future.getChannel().isConnected()) {
            LOGGER.error("[YARPC] channel " + address + " not connected.");
            throw new YarpcException(YarpcCode.YARPC_CLIENT_CONN_FAILED, "channel not connected.", future.getCause());
        }
        NettyClient client = new NettyClient(future.getChannel());
        return client;
    }

}
