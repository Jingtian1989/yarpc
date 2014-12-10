package org.yarpc.common.proxy;

import org.yarpc.common.domain.Metadata;
import org.yarpc.common.domain.RemoteRequest;
import org.yarpc.common.domain.RemoteResponse;
import org.yarpc.common.service.ConsumerService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by jingtian.zjt on 2014/12/7.
 */
public class JDKProxy implements InvocationHandler{

    private Metadata metadata;
    private ConsumerService processor;
    public JDKProxy(ConsumerService processor, Metadata metadata) {
        this.processor = processor;
        this.metadata = metadata;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RemoteRequest request = new RemoteRequest();
        request.setTarget(metadata.getName());
        request.setMethod(method.getName());
        request.setArgs(args);
        String[] argTypes = new String[method.getParameterTypes().length];
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            argTypes[i] = method.getParameterTypes()[i].getName();
        }
        request.setArgTypes(argTypes);
        RemoteResponse response = processor.handleRequest(request, metadata);
        if (response.getData() instanceof Throwable) {
            throw (Throwable) response.getData();
        }
        return response.getData();
    }
}
