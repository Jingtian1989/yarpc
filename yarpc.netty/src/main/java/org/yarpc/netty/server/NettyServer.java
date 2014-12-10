package org.yarpc.netty.server;

import org.yarpc.netty.codecs.NettyProtocolDecoder;
import org.yarpc.netty.codecs.NettyProtocolEncoder;
import org.yarpc.common.server.BaseServer;
import org.yarpc.common.service.ProviderService;
import org.yarpc.common.thread.ThreadPoolManager;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by jingtian.zjt on 2014/12/3.
 */
public class NettyServer extends BaseServer {

    private final ServerBootstrap bootstrap;
    private final AtomicBoolean started = new AtomicBoolean(false);

    private final NettyServerFirewall firewall;
    private final NettyServerHandler handler;

    public NettyServer(String host, int port, ProviderService processor) {
        super(host, port);
        firewall = new NettyServerFirewall();
        handler = new NettyServerHandler(processor);
        java.util.concurrent.ThreadFactory master = new ThreadPoolManager.NamedThreadFactory("[YARPC-SERVER-MASTER]");
        java.util.concurrent.ThreadFactory worker = new ThreadPoolManager.NamedThreadFactory("[YARPC-SERVER-WORKER]");
        bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(master),
                Executors.newCachedThreadPool(worker)));
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);
    }

    @Override
    public void startInternal() {
        if (!started.compareAndSet(false, true)) {
            return;
        }
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = new DefaultChannelPipeline();
                pipeline.addLast("firewall", firewall);
                pipeline.addLast("decoder", new NettyProtocolDecoder());
                pipeline.addLast("encoder", new NettyProtocolEncoder());
                pipeline.addLast("handler", handler);
                return pipeline;
            }
        });
        bootstrap.bind(new InetSocketAddress(this.host, this.port));
    }

    @Override
    public void stopInternal() {
        bootstrap.releaseExternalResources();
        started.set(false);
    }

    @Override
    public void refuseConnect() {
        firewall.setRefused(true);
        for (Channel channel : handler.getChannels()) {
            channel.close();
        }
    }

    @Override
    public void openConnect() {
        firewall.setRefused(false);
    }

}
