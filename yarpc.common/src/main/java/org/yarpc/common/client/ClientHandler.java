package org.yarpc.common.client;

import org.yarpc.common.domain.Metadata;
import org.yarpc.common.domain.RemoteRequest;
import org.yarpc.common.domain.RemoteResponse;
import org.yarpc.common.exception.RPCException;

/**
 * Created by jingtian.zjt on 2014/12/7.
 */
public interface ClientHandler{

    public RemoteResponse handleInvoke(Client client, RemoteRequest request, Metadata metadata) throws RPCException;
}
