package org.yarpc.common.client;

import java.util.EventListener;

/**
 * Created by jingtian.zjt on 2014/12/7.
 */
public interface ClientFutureListener extends EventListener{

    public void complete(ClientCallBack future);
}
