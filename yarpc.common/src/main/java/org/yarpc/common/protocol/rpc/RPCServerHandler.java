package org.yarpc.common.protocol.rpc;

import org.yarpc.common.codec.Codecs;
import org.yarpc.common.codec.Encoder;
import org.yarpc.common.domain.RemoteResponse;
import org.yarpc.common.domain.RemoteRequest;
import org.yarpc.common.protocol.ProtocolSetting;
import org.yarpc.common.server.Connection;
import org.yarpc.common.server.ServerHandler;
import org.yarpc.common.service.ProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yarpc.common.protocol.ProtocolStatus;

import java.util.Date;
import java.util.concurrent.Executor;

/**
 * Created by jingtian.zjt on 2014/12/3.
 */
public class RPCServerHandler implements ServerHandler<RPCRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RPCServerHandler.class);

    @Override
    public Executor getExecutor(ProviderService processor, RPCRequest request) {
        return processor.getExecutor(new String(request.getTarget(), ProtocolSetting.DEFAULT_CHARSET));
    }

    @Override
    public RPCResponse handleRequest(ProviderService processor, RPCRequest request, Connection connection) {
        RemoteResponse remoteResponse = null;
        try {
            RemoteRequest remoteRequest = buildRequest(request);
            remoteResponse = processor.handleRequest(remoteRequest, connection);
            if (remoteResponse.isError()) {
                byte[] response = remoteResponse.getError().getBytes(ProtocolSetting.DEFAULT_CHARSET);
                return new RPCResponse(request.getRequestID(), request.getCodecType(), ProtocolStatus.ERROR, response);
            }
        } catch (Exception e) {
            LOGGER.error("[REMOTE] process exception:", e);
            byte[] response = ("please check log on server that unknown exception occured @"
                    + new Date() + " on connection " + connection).getBytes(ProtocolSetting.DEFAULT_CHARSET);
            return new RPCResponse(request.getRequestID(), request.getCodecType(), ProtocolStatus.ERROR, response);
        }
        Encoder encoder = Codecs.getEncoder(request.getCodecType());
        try {
            byte[] response = encoder.encode(remoteResponse.getData());
            return new RPCResponse(request.getRequestID(), request.getCodecType(), ProtocolStatus.OK, response);
        } catch (Exception e) {
            LOGGER.error("[REMOTE] codecs exception:", e);
            byte[] response = ("please check log on server that codecs exception occured @"
                    + new Date() +" on connection " + connection).getBytes(ProtocolSetting.DEFAULT_CHARSET);
            return new RPCResponse(request.getRequestID(), request.getCodecType(), ProtocolStatus.ERROR, response);
        }
    }

    private RemoteRequest buildRequest(RPCRequest request) throws Exception{
        RemoteRequest remoteRequest = new RemoteRequest();
        Object[] args = new Object[request.getArgs().length];
        //解码参数
        for (int i = 0; i < request.getArgs().length; i++) {
            args[i] = Codecs.getDecoder(request.getCodecType()).decode(request.getArgs()[i]);
        }
        remoteRequest.setArgs(args);

        String[] argTypes = new String[request.getArgTypes().length];
        //解码参数类型
        for (int i = 0; i < request.getArgTypes().length; i++) {
            argTypes[i] = new String(request.getArgTypes()[i], ProtocolSetting.DEFAULT_CHARSET);
        }
        remoteRequest.setArgTypes(argTypes);

        //服务名、方法名
        remoteRequest.setMethod(new String(request.getMethod(), ProtocolSetting.DEFAULT_CHARSET));
        remoteRequest.setTarget(new String(request.getTarget(), ProtocolSetting.DEFAULT_CHARSET));
        return remoteRequest;
    }

}
