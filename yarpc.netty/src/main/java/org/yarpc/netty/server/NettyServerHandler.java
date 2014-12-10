package org.yarpc.netty.server;

import org.jboss.netty.channel.*;
import org.yarpc.common.server.Connection;
import org.yarpc.common.server.ServerHandler;
import org.yarpc.common.service.ProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yarpc.common.protocol.BaseRequest;
import org.yarpc.common.protocol.BaseResponse;
import org.yarpc.common.protocol.ProtocolStatus;
import org.yarpc.common.util.NetUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by jingtian.zjt on 2014/12/3.
 */
public class NettyServerHandler extends SimpleChannelUpstreamHandler{

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerHandler.class);
    private final List<Channel> channels = new CopyOnWriteArrayList<Channel>();
    private ProviderService processor;

    public NettyServerHandler(ProviderService processor) {
        super();
        this.processor = processor;

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {

    }
    @Override
    public void messageReceived(final ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object message = e.getMessage();
        if (!(message instanceof BaseRequest) && !(message instanceof List)) {
            throw new Exception("[YARPC] unsupported message type from " + ctx.getChannel().getRemoteAddress());
        }
        handleRequest(ctx, message);
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        channels.remove(ctx.getChannel());
        super.channelDisconnected(ctx, e);
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        channels.add(ctx.getChannel());
        super.channelConnected(ctx, e);
    }


    private void handleRequest(final ChannelHandlerContext ctx, final Object message) {
        final Connection connection = new NettyConnection(ctx.getChannel());
        if (message instanceof List) {
            List<BaseRequest> messages = (List<BaseRequest>) message;
            for (Object object : messages) {
                processRequest(object, connection);
            }
        } else {
            processRequest(message, connection);
        }
    }

    private void processRequest(final Object message, final Connection connection) {
        BaseRequest request = (BaseRequest) message;
        ServerHandler<BaseRequest> serverHandler = (ServerHandler<BaseRequest>) request.getServerHandler();
        if (serverHandler.getExecutor(processor, request) == null) {
            BaseResponse wrapper = serverHandler.handleRequest(processor, request, connection);
            connection.write(wrapper);
        } else {
            try {
                serverHandler.getExecutor(processor, request).execute(new Handler(connection, request, serverHandler, processor));
            } catch (Exception e) {
                LOGGER.error("[YARPC] thread pool is full.");
                BaseResponse response = request.createErrorResponse(ProtocolStatus.REFUSED, "thread pool is full");
                connection.write(response);
            }
        }

    }
    public List<Channel> getChannels() {
        return channels;
    }


    private static class Handler implements Runnable {
        private final Connection connection;
        private final BaseRequest request;
        private final ServerHandler<BaseRequest> serverHandler;
        private final long dispatchTime = System.currentTimeMillis();
        private final ProviderService processor;

        public Handler(Connection connection, BaseRequest request, ServerHandler<BaseRequest> serverHandler, ProviderService processor) {
            this.connection = connection;
            this.request = request;
            this.serverHandler = serverHandler;
            this.processor = processor;
        }

        public void run() {
            try {
                long begin = System.currentTimeMillis();
                long pending = begin - dispatchTime;
                int clientTimeout = request.getTimeout();
                if (clientTimeout > 0 && pending >= clientTimeout) {
                    LOGGER.error("[YARPC] drop the request for pending too long in queue. pending:" + pending );
                    return;
                }
                BaseResponse wrapper = serverHandler.handleRequest(processor, request, connection);
                if (clientTimeout > 0 && System.currentTimeMillis() - begin >= clientTimeout) {
                    LOGGER.error("[YARPC] refuse to send response for cost too much time to handle the request. pending:"
                            + pending + ", cost:" + (System.currentTimeMillis() - begin) + ", timeout:"
                            + clientTimeout);
                }
                connection.write(wrapper);
            } catch (Exception e) {
                LOGGER.error("[YARPC] unexpected application exception when handle the request. exception:",e);
                BaseResponse response = request.createErrorResponse(ProtocolStatus.ERROR,
                        "unexpected application exception @" + NetUtil.getLocalAddress());
                connection.write(response);
            }
        }
    }


}
