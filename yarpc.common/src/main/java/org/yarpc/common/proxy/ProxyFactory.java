package org.yarpc.common.proxy;

import org.yarpc.common.domain.Metadata;
import org.yarpc.common.service.ConsumerService;

import java.lang.reflect.Proxy;

/**
 * Created by jingtian.zjt on 2014/12/7.
 */
public class ProxyFactory {

    public static final int JDK_PROXY = 1;

    public static Object createProxy(ConsumerService processor, ClassLoader classLoader, Class<?>[] classes, int type, Metadata metadata){
        switch (type) {
            case JDK_PROXY:
                return createJDKProxy(processor, classLoader, classes, metadata);
            default:
                throw new RuntimeException("[REMOTE] unsupported proxy type:" + type);
        }
    }

    private static Object createJDKProxy(ConsumerService processor, ClassLoader classLoader, Class<?>[] classes, Metadata metadata) {
        return Proxy.newProxyInstance(classLoader, classes, new JDKProxy(processor, metadata));
    }

}
