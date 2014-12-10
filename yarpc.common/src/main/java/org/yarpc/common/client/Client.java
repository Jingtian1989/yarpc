package org.yarpc.common.client;

import org.yarpc.common.exception.RemoteException;
import org.yarpc.common.protocol.BaseRequest;
import org.yarpc.common.protocol.BaseResponse;

/**
 * Created by jingtian.zjt on 2014/12/7.
 */
public interface Client {

    public BaseResponse syncInvoke(BaseRequest request) throws RemoteException;

    public boolean isConnected();

}
