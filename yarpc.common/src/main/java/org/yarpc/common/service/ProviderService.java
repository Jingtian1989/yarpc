package org.yarpc.common.service;

import org.yarpc.common.api.ProviderBean;
import org.yarpc.common.domain.Metadata;
import org.yarpc.common.thread.ThreadPoolManager;
import org.yarpc.common.domain.RemoteRequest;
import org.yarpc.common.domain.RemoteResponse;
import org.yarpc.common.server.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * Created by jingtian.zjt on 2014/12/6.
 */
public class ProviderService {

    private int corePoolSize = 8;
    private int maxmumPoolSize = 16;
    private int queueSize = 256;
    private final ConcurrentHashMap<String, ProviderSignature> providers;
    private final ThreadPoolManager threadPoolManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderService.class);

    public ProviderService() {
        threadPoolManager = new ThreadPoolManager(corePoolSize, maxmumPoolSize, queueSize);
        providers = new ConcurrentHashMap<String, ProviderSignature>();
    }

    public synchronized void registerProvider(final ProviderBean provider) {
        String name = provider.getMetadata().getName();
        ProviderSignature signature = providers.get(name);
        if (signature == null) {
            signature = new ProviderSignature(provider);
            providers.put(name, signature);
        }
        threadPoolManager.allocThreadPool(name, provider.getCorePoolSize(),
                provider.getMaxPoolSize());
    }

    public ProviderBean getProvider(String name) {
        return providers.get(name).getProvider();
    }

    public Executor getExecutor(String name) {
        return threadPoolManager.getThreadExecutor(name);
    }

    public RemoteResponse handleRequest(RemoteRequest request, Connection connection) {

        final ProviderSignature signature = providers.get(request.getTarget());
        final RemoteResponse response = new RemoteResponse();
        if (signature == null) {
            response.setError("[RPC] can not find the required provider: " + request.getTarget());
            LOGGER.error("[RPC] can not find the required provider: " + request.getTarget());
            return response;
        }
        final ProviderBean provider = signature.getProvider();
        final Metadata metadata = provider.getMetadata();
        final Method method = signature.getMethod(request.getMethod(), request.getArgTypes());
        if (method == null) {
            response.setError("[RPC] can not find the required method:" + request.getMethod() + "@" + request.getTarget());
            LOGGER.error("[RPC] can not find the required method:" + request.getMethod() + "@" + request.getTarget());
            return response;
        }
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(metadata.getClassLoader());
            Object result = method.invoke(metadata.getTarget(), request.getArgs());
            response.setData(result);
        } catch (InvocationTargetException e) {
            traceCause(e.getTargetException());
            response.setData(e.getTargetException());
        } catch (Throwable t) {
            String log = "[RPC] invoke process method encounter unknown exception. process:" + provider
                    + ", request:" + request;
            response.setError(log);
            LOGGER.error("[RPC] invoke process method encounter unknown exception. process:" + provider
                    + ", request:" + request + ", client:" + connection.getRemoteAddress());
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
        return response;
    }

    private void traceCause(Throwable t) {
        Throwable root = t;
        while (null != root.getCause()) {
            root = root.getCause();
        }
        if (root != t) {
            try {
                t.setStackTrace(root.getStackTrace());
            }catch (Exception e) {
                LOGGER.error("[RPC] trace application exception failed.", e);
            }
        }
    }

    private static class ProviderSignature {

        private ProviderBean bean;
        private ConcurrentHashMap<MethodSignature, Method> methods;
        public ProviderSignature(final ProviderBean bean) {
            this.bean = bean;
            this.methods = new ConcurrentHashMap<MethodSignature, Method>();
            //获取方法
            Method[] proxyMethods = bean.getMetadata().getProxyClazz().getDeclaredMethods();
            for (Method method : proxyMethods) {
                methods.put(MethodSignature.format(method), method);
            }
        }

        public ProviderBean getProvider() {
            return bean;
        }

        public Method getMethod(String name, String[] parameterTypes) {
            return methods.get(MethodSignature.format(name, parameterTypes));
        }


    }

    private static class MethodSignature {

        private String name;
        private String[] paramterTypes;

        public MethodSignature(String name, String[] paramterTypes) {
            this.name = name;
            this.paramterTypes = paramterTypes;
        }

        public String getName() {
            return name;
        }

        public String[] getParamterTypes() {
            return paramterTypes;
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof MethodSignature)) {
                return false;
            }
            MethodSignature signature = (MethodSignature) object;
            if (name.equals(signature.getName()) && Arrays.equals(paramterTypes, signature.getParamterTypes())) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        public static MethodSignature format(String name, String[] parameterTypes) {
            return new MethodSignature(name, parameterTypes);
        }

        public static MethodSignature format(Method method) {
            String name = method.getName();
            String[] parameterTypes = new String[method.getParameterTypes().length];
            for (int i = 0; i < method.getParameterTypes().length; i++) {
                parameterTypes[i] = method.getParameterTypes()[i].getName();
            }
            return format(name, parameterTypes);
        }
    }


}
