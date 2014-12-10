package org.yarpc.common.client;

import java.util.EventListener;

/**
 * Created by jingtian.zjt on 2014/12/7.
 */
public interface RemoteFutureListener extends EventListener{

    public void complete(RemoteCallBack future);
}
