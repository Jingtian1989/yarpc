package org.yarpc.common.client;

import org.yarpc.common.exception.RemoteException;
import org.yarpc.common.protocol.BaseRequest;
import org.yarpc.common.protocol.BaseResponse;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by jingtian.zjt on 2014/12/7.
 */
public abstract class BaseClient implements Client {

    private final ConcurrentHashMap<Long, RemoteCallBack> responses = new ConcurrentHashMap<Long, RemoteCallBack>();

    @Override
    public BaseResponse syncInvoke(BaseRequest request) throws RemoteException {
        RemoteCallBack callBack = new RemoteCallBack();
        responses.put(request.getRequestID(), callBack);
        send(request);
        return callBack.get(request.getTimeout(), TimeUnit.MILLISECONDS);
    }

    public void complete(BaseResponse response){
        RemoteCallBack callBack = responses.remove(response.getRequestID());
        if (callBack != null) {
            callBack.complete(response);
        }
    }

    public abstract void send(BaseRequest request) throws RemoteException;
}
