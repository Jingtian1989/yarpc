package org.yarpc.common.server;


import org.yarpc.common.protocol.BaseRequest;
import org.yarpc.common.protocol.BaseResponse;
import org.yarpc.common.service.ProviderService;

import java.util.concurrent.Executor;

/**
 * Created by jingtian.zjt on 2014/12/3.
 */
public interface ServerHandler<T extends BaseRequest> {

    public Executor getExecutor(ProviderService processor, final T request);

    public BaseResponse handleRequest(ProviderService processor, final T request, final Connection connection);

}
