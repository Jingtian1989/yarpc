package org.yarpc.common.protocol.rpc;

import org.yarpc.common.buffer.ByteBufferWrapper;
import org.yarpc.common.protocol.BaseResponse;
import org.yarpc.common.protocol.Protocol;
import org.yarpc.common.protocol.ProtocolFactory;
import org.yarpc.common.protocol.ProtocolStatus;

/**
 * Created by jingtian.zjt on 2014/12/3.
 */
public class RPCResponse extends BaseResponse {

    private final static Protocol protocol = ProtocolFactory.getInstance().getProtocol(RPCProtocol.RPC_PROTOCOL);
    private final byte codecType;
    private final byte[] response;
    private final ProtocolStatus status;

    public RPCResponse(long requestId, byte codecType, ProtocolStatus status, byte[] response) {
        super(RPCProtocol.RPC_PROTOCOL, requestId);
        this.codecType = codecType;
        this.response = response;
        this.status = status;
    }

    public byte getCodecType() {
        return codecType;
    }

    public byte[] getResponse() {
        return response;
    }

    public ProtocolStatus getStatus() {
        return status;
    }

    @Override
    public void encode(ByteBufferWrapper wrapper) throws Exception {
        protocol.encode(this, wrapper);
    }
}
