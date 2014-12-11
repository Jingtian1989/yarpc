package org.yarpc.common.service;

import org.yarpc.common.api.ConsumerBean;
import org.yarpc.common.client.Client;
import org.yarpc.common.client.ClientFactory;
import org.yarpc.common.client.ClientHandler;
import org.yarpc.common.domain.Metadata;
import org.yarpc.common.domain.RemoteRequest;
import org.yarpc.common.domain.RemoteResponse;
import org.yarpc.common.exception.YarpcException;
import org.yarpc.common.protocol.ProtocolFactory;
import org.yarpc.common.proxy.ProxyFactory;
import org.yarpc.common.router.Router;

import java.net.SocketAddress;

/**
 * Created by jingtian.zjt on 2014/12/7.
 */
public class ConsumerService {

    private ClientFactory clientFactory;
    private Router router;
    public ConsumerService(ClientFactory clientFactory, Router router) {
        this.clientFactory = clientFactory;
        this.router = router;
    }

    public Object registerConsumer(ConsumerBean consumer) {
        ClassLoader classLoader = consumer.getMetadata().getClassLoader();
        Class<?>[] classes = new Class[]{consumer.getMetadata().getProxyClazz()};
        return ProxyFactory.createProxy(this, classLoader, classes, consumer.getProxyType(), consumer.getMetadata());
    }

    public RemoteResponse handleRequest(RemoteRequest request, Metadata metadata) throws YarpcException {
        SocketAddress address = router.route(metadata);
        ClientHandler handler = ProtocolFactory.getInstance().getClientHandler(metadata.getProtocol());
        Client client = clientFactory.get(address);
        return handler.handleInvoke(client, request, metadata);
    }

}
