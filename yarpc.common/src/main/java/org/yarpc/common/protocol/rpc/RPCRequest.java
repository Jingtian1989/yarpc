package org.yarpc.common.protocol.rpc;

import org.yarpc.common.buffer.ByteBufferWrapper;
import org.jingtian.remote.protocol.*;
import org.yarpc.common.protocol.*;
import org.yarpc.common.server.ServerHandler;

/**
 * Created by jingtian.zjt on 2014/12/3.
 */
public class RPCRequest extends BaseRequest {
    private final static ServerHandler<? extends BaseRequest> serverHandler = ProtocolFactory.getInstance().
            getServerHandler(RPCProtocol.RPC_PROTOCOL);
    private final static Protocol protocol = ProtocolFactory.getInstance().getProtocol(RPCProtocol.RPC_PROTOCOL);


    private final byte[] target;
    private final byte[] method;
    private final byte[][] argTypes;
    private final byte[][] args;
    private final byte[] properties;
    private final byte codecType;


    public RPCRequest(long id, int timeout, byte[] target, byte[] method,
                      byte[][] argTypes, byte[][] args, byte[] properties, byte codecType) {
        super(RPCProtocol.RPC_PROTOCOL, id, timeout);
        this.target = target;
        this.method = method;
        this.argTypes = argTypes;
        this.args = args;
        this.properties = properties;
        this.codecType  = codecType;
    }

    public byte[] getProperties() {
        return properties;
    }

    public byte[] getTarget() {
        return target;
    }

    public byte[] getMethod() {
        return method;
    }

    public byte[][] getArgTypes() {
        return argTypes;
    }

    public byte[][] getArgs() {
        return args;
    }

    public byte getCodecType(){
        return codecType;
    }

    @Override
    public ServerHandler<? extends BaseRequest> getServerHandler() {
        return serverHandler;
    }

    @Override
    public BaseResponse createErrorResponse(ProtocolStatus status, String errorMessage) {
        return new RPCResponse(this.getRequestID(), this.codecType, status,
                errorMessage.getBytes(ProtocolSetting.DEFAULT_CHARSET));
    }

    @Override
    public void encode(ByteBufferWrapper wrapper) throws Exception {
        protocol.encode(this, wrapper);
    }
}
