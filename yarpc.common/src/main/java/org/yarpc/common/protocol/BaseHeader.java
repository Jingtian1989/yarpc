package org.yarpc.common.protocol;

import org.yarpc.common.buffer.ByteBufferWrapper;

/**
 * Created by jingtian.zjt on 2014/12/3.
 */
public abstract class BaseHeader {

    private final int protocolType;
    private final long requestID;

    public BaseHeader(int protocolType, long requestID) {
        this.protocolType = protocolType;
        this.requestID = requestID;
    }

    public int getProtocolType() {
        return protocolType;
    }

    public long getRequestID() {
        return requestID;
    }

    public abstract void encode(ByteBufferWrapper wrapper) throws Exception;
}
