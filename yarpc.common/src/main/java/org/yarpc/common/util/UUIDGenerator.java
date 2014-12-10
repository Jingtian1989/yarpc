package org.yarpc.common.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by jingtian.zjt on 2014/12/8.
 */
public class UUIDGenerator {
    private static AtomicLong generator = new AtomicLong();

    public static long get() {
        return generator.getAndDecrement();
    }
}
