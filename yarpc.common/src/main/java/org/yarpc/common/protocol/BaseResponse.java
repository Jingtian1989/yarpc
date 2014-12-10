package org.yarpc.common.protocol;

/**
 * Created by jingtian.zjt on 2014/12/3.
 */
public abstract class BaseResponse extends BaseHeader {

    public BaseResponse(int protocolType, long requestID) {
        super(protocolType, requestID);
    }

}
