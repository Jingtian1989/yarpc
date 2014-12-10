package org.yarpc.test;

import org.yarpc.common.api.ConsumerBean;
import org.yarpc.common.api.ProviderBean;
import org.yarpc.common.client.ClientFactory;
import org.yarpc.netty.client.NettyClientFactory;
import org.yarpc.common.router.DefaultRouter;
import org.yarpc.common.router.Router;
import org.yarpc.netty.server.NettyServer;
import org.yarpc.common.server.Server;
import org.yarpc.common.service.ConsumerService;
import org.yarpc.common.service.ProviderService;

/**
 * Created by jingtian.zjt on 2014/12/8.
 */
public class Bootstrap {

    public static void main(String args[]) {


        Router router = new DefaultRouter("127.0.0.1", 8009);
        ClientFactory clientFactory = new NettyClientFactory();
        ConsumerService consumerService = new ConsumerService(clientFactory, router);

        ProviderService providerService = new ProviderService();
        Server server = new NettyServer("127.0.0.1", 8009, providerService);
        server.start();


        HelloWorldService helloWorldService = new HelloWorldServiceImpl();
        ProviderBean providerBean = new ProviderBean();
        providerBean.setTarget(helloWorldService);
        providerBean.setIfClass("org.yarpc.test.HelloWorldService");
        providerBean.setName("helloWorldService");
        providerBean.init();

        providerService.registerProvider(providerBean);

        ConsumerBean consumerBean = new ConsumerBean();
        consumerBean.setIfClass("org.yarpc.test.HelloWorldService");
        consumerBean.setName("helloWorldService");
        consumerBean.init();

        HelloWorldService proxy = (HelloWorldService)consumerService.registerConsumer(consumerBean);
        String hello = proxy.sayHello("remote");
        System.out.println(hello);

        try {
            proxy.throwException("remote exception");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
