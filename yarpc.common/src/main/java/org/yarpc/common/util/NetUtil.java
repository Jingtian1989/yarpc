package org.yarpc.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by jingtian.zjt on 2014/12/3.
 */
public class NetUtil {
    public static final Logger LOGGER = LoggerFactory.getLogger(NetUtil.class);

    public static String getLocalAddress() {
        String host = "";
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            LOGGER.error("[REMOTE] get local address failed.", e);
        }
        return host;
    }

}
