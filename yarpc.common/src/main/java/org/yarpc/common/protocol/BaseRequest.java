package org.yarpc.common.protocol;

import org.yarpc.common.server.ServerHandler;

/**
 * Created by jingtian.zjt on 2014/12/3.
 */
public abstract class BaseRequest extends BaseHeader {

    private final int timeout;
    public BaseRequest(int protocolType, long id, int timeout) {
        super(protocolType, id);
        this.timeout = timeout;
    }

    public abstract ServerHandler<? extends BaseRequest> getServerHandler();

    public int getTimeout() {
        return timeout;
    }

    public abstract BaseResponse createErrorResponse(ProtocolStatus status, String errorMessage);
}
