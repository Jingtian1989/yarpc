package org.yarpc.common.thread;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jingtian.zjt on 2014/12/5.
 */
public class ThreadPoolManager {

    private static final long keepAliveTime = 300l;
    private final ThreadPoolExecutor defaultPoolExecutor;
    private final Map<String, ThreadPoolExecutor> poolCache = new HashMap<String, ThreadPoolExecutor>();
    private RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
    public ThreadPoolManager(int corePoolSize, int maxmumPoolSize, int queueSize) {
        final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(queueSize);
        final ThreadFactory threadFactory = new NamedThreadFactory("ProviderProcessor");
        defaultPoolExecutor = new ThreadPoolExecutor(corePoolSize, maxmumPoolSize, keepAliveTime, TimeUnit.SECONDS,
                workQueue, threadFactory, handler);

    }

    public void allocThreadPool(final String name, int corePoolSize, int maxmumPoolSize) {
        if (poolCache.containsKey(name)) {
            throw new RuntimeException("[YARPC] duplicated thread pool allocation for process:" + name);
        }

        if (defaultPoolExecutor == null || defaultPoolExecutor.isShutdown()) {
            throw new RuntimeException("[YARPC] can not allocate thread pool for process:" + name);
        }

        int balance = defaultPoolExecutor.getMaximumPoolSize();
        //剩余线程数量小于申请的线程数量
        if (balance < maxmumPoolSize) {
            throw new RuntimeException(MessageFormat.format("[YARPC] thread pool allocated failed for process {0}: balance {1} require {2}.",
                    name, balance, maxmumPoolSize));
        }

        ThreadFactory threadFactory = new NamedThreadFactory("ProviderProcessor-" + name);
        try {
            ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maxmumPoolSize, keepAliveTime,
                    TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), threadFactory, handler);
            poolCache.put(name, executor);

        } catch (Exception e) {
            throw new RuntimeException("[YARPC] thread pool allocated failed.", e);
        }
        int newBalance = balance - maxmumPoolSize;
        if (newBalance == 0) {
            defaultPoolExecutor.shutdown();
        } else {
            if (newBalance < defaultPoolExecutor.getCorePoolSize()) {
                defaultPoolExecutor.setCorePoolSize(newBalance);
            }
            defaultPoolExecutor.setMaximumPoolSize(newBalance);
        }
    }


    public Executor getThreadExecutor(String name) {
        if (!poolCache.isEmpty()) {
            ThreadPoolExecutor executor = poolCache.get(name);
            if (executor != null) {
                return executor;
            }
        }
        return defaultPoolExecutor;
    }

    public void shutdown() {
        if (defaultPoolExecutor != null && !defaultPoolExecutor.isShutdown()) {
            defaultPoolExecutor.shutdown();
        }

        if (!poolCache.isEmpty()) {
            Iterator<ThreadPoolExecutor> iterator = poolCache.values().iterator();
            while (iterator.hasNext()) {
                ThreadPoolExecutor poolExecutor = iterator.next();
                poolExecutor.shutdown();
            }
        }
    }
    public static class NamedThreadFactory implements java.util.concurrent.ThreadFactory {

        private static final AtomicInteger poolNumber = new AtomicInteger(1);

        private final AtomicInteger threadNumber = new AtomicInteger(1);

        private final ThreadGroup group;

        private final String namePrefix;

        private final boolean isDaemon;

        public NamedThreadFactory() {
            this("pool");
        }

        public NamedThreadFactory(String name) {
            this(name, true);
        }

        public NamedThreadFactory(String prefix, boolean daemon) {
            SecurityManager securityManager = System.getSecurityManager();
            group = (securityManager != null) ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = prefix + "-" + poolNumber.getAndIncrement() + "-thread-";
            isDaemon = daemon;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            t.setDaemon(isDaemon);
            return t;
        }
    }
}
