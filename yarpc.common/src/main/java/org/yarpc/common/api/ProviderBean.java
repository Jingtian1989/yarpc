package org.yarpc.common.api;

import org.yarpc.common.domain.Metadata;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by jingtian.zjt on 2014/12/6.
 */
public class ProviderBean {

    private Metadata metadata;
    private AtomicBoolean inited = new AtomicBoolean(false);
    private int corePoolSize = 1;
    private int maxPoolSize = 1;

    public ProviderBean() {
        metadata = new Metadata();
    }

    public void init() {
        if (!inited.compareAndSet(false, true)) {
            return;
        }
        checkConfig();
    }

    public void checkConfig() {
        //代理对象
        if (metadata.getTarget() == null) {
            throw new IllegalArgumentException("[REMOTE] target should not be null.");
        }
        //代理接口
        Class<?> proxyClass = null;
        try {
            proxyClass = Class.forName(metadata.getIfClass());
            metadata.setProxyClass(proxyClass);
            if (!proxyClass.isInterface()) {
                throw new IllegalArgumentException("[REMOTE] proxy should be interface.");
            }

            if (!proxyClass.isAssignableFrom(metadata.getTarget().getClass())) {
                throw new IllegalArgumentException("[REMOTE] target should implement proxy class.");
            }


        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("[REMOTE] proxy class does not exist.");
        }
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }


    public void setIfClass(String proxy) {
        metadata.setIfClass(proxy);
    }

    public void setName(String name) {
        metadata.setName(name);
    }

    public void setTarget(Object target) {
        metadata.setTarget(target);
    }



}
