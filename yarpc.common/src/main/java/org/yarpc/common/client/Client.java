package org.yarpc.common.client;

import org.yarpc.common.exception.RPCException;
import org.yarpc.common.protocol.BaseRequest;
import org.yarpc.common.protocol.BaseResponse;

/**
 * Created by jingtian.zjt on 2014/12/7.
 */
public interface Client {

    public BaseResponse syncInvoke(BaseRequest request) throws RPCException;

    public boolean isConnected();

}
