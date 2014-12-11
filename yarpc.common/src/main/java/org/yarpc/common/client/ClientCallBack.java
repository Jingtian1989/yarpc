package org.yarpc.common.client;

import org.yarpc.common.exception.ExceptionCode;
import org.yarpc.common.exception.RemoteException;
import org.yarpc.common.protocol.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Created by jingtian.zjt on 2014/12/7.
 */
public class ClientCallBack {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientCallBack.class);

    private BaseResponse response;
    private ClientFutureListener listener;
    private final CountDownLatch latch = new CountDownLatch(1);

    public BaseResponse get(long timeout, TimeUnit unit) throws RemoteException{
        try {
            if (!latch.await(timeout, unit)) {
                throw new RemoteException(ExceptionCode.YARPC_CLIENT_CONN_TIMEOUT, "request timeout.");
            }
        } catch (InterruptedException e) {
            LOGGER.error("[YARPC] wait response failed. exception:", e);
            throw new RemoteException(ExceptionCode.YARPC_CLIENT_CONN_FAILED, "request interrupted.");
        }
        return response;
    }

    public void complete(final BaseResponse response) {
        synchronized (this) {
            if (this.response == null) {
                this.response = response;
            } else {
                return;
            }
        }
        latch.countDown();
        if (listener != null) {
            listener.complete(this);
        }
    }

    public void addListener(ClientFutureListener listener) {
        this.listener = listener;
    }

}
