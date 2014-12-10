package org.yarpc.common.util;

import com.sun.management.HotSpotDiagnosticMXBean;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by jingtian.zjt on 2014/12/3.
 */
public class JVMUtil {

    private static volatile MemoryMXBean memoryBean;
    private static volatile HotSpotDiagnosticMXBean hotspotBean;
    private static final String HOTSPOT_BEAN_NAME = "com.sun.management:type=HotSpotDiagnostic";

    public static void jmap(String file, boolean live) throws Exception {
        initHotspotMBean();
        hotspotBean.dumpHeap(file, live);
    }

    public static void jstack(OutputStream stream) throws Exception {
        try {
            Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
            Iterator<Map.Entry<Thread, StackTraceElement[]>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Thread, StackTraceElement[]> entry = iterator.next();
                StackTraceElement[] elements = entry.getValue();
                if (elements != null && elements.length > 0) {
                    String name = entry.getKey().getName();
                    stream.write(("thread name:[" + name + "]\n").getBytes());
                    for (StackTraceElement element : elements) {
                        String stack = element.toString() + "\n";
                        stream.write(stack.getBytes());
                    }
                    stream.write("\n".getBytes());

                }
            }
        } catch (Exception e) {
            throw e;
        }
    }


    public static double memoryUsed(OutputStream stream) throws Exception {
        initMemoryBean();
        stream.write("-----------------------------------------Memory Used----------------------------------------\n".getBytes());
        String heapMemoryUsed = memoryBean.getHeapMemoryUsage().toString() + "\n";
        stream.write(("heap memory used:" + heapMemoryUsed).getBytes());
        String nonHeapMemoryUsed = memoryBean.getNonHeapMemoryUsage().toString();
        stream.write(("nonheap memory used:" + nonHeapMemoryUsed).getBytes());
        return memoryBean.getHeapMemoryUsage().getUsed() / memoryBean.getHeapMemoryUsage().getMax();

    }

    public static synchronized void initMemoryBean() throws Exception {
        if (memoryBean == null) {
            memoryBean = getMemoryMBean();
        }
    }

    private static MemoryMXBean getMemoryMBean() throws Exception {
        return AccessController.doPrivileged(new PrivilegedExceptionAction<MemoryMXBean>() {
            @Override
            public MemoryMXBean run() throws Exception {
                return ManagementFactory.getMemoryMXBean();
            }
        });
    }

    private static HotSpotDiagnosticMXBean getHotspotMBean() throws Exception{
        return AccessController.doPrivileged(new PrivilegedExceptionAction<HotSpotDiagnosticMXBean>() {
            @Override
            public HotSpotDiagnosticMXBean run() throws Exception {
                MBeanServer server = ManagementFactory.getPlatformMBeanServer();
                Set<ObjectName> set = server.queryNames(new ObjectName(HOTSPOT_BEAN_NAME), null);
                Iterator<ObjectName> iterator = set.iterator();
                if (iterator.hasNext()) {
                    ObjectName name = iterator.next();
                    HotSpotDiagnosticMXBean bean = ManagementFactory.newPlatformMXBeanProxy(server, name.toString(), HotSpotDiagnosticMXBean.class);
                    return bean;
                } else {
                    return null;
                }

            }
        });
    }

    private static synchronized void initHotspotMBean() throws  Exception{
        if (hotspotBean == null) {
            hotspotBean = getHotspotMBean();
        }
    }


}
