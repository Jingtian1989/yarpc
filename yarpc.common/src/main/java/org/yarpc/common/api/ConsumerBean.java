package org.yarpc.common.api;

import org.yarpc.common.domain.Metadata;
import org.yarpc.common.proxy.ProxyFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by jingtian.zjt on 2014/12/7.
 */
public class ConsumerBean {

    private Metadata metadata;
    private int proxyType = ProxyFactory.JDK_PROXY;
    private final AtomicBoolean inited = new AtomicBoolean(false);

    public ConsumerBean() {
        metadata = new Metadata();
    }

    public void init() {
        if (!inited.compareAndSet(false, true)) {
            return;
        }
        checkconfig();
    }

    public void setProxyType(int proxyType) {
        this.proxyType = proxyType;
    }

    public int getProxyType() {
        return proxyType;
    }

    public void setIfClass(String ifClass) {
        metadata.setIfClass(ifClass);
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setName(String name) {
        metadata.setName(name);
    }


    public void checkconfig() {
        String proxy = metadata.getIfClass();
        if (proxy == null) {
            throw new IllegalArgumentException("[YARPC] proxy should not be null.");
        }

        Class<?> proxyClass = null;
        try {
            proxyClass = Class.forName(proxy);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("[YARPC] proxy class does not exist.");
        }
        metadata.setProxyClass(proxyClass);
    }


}
