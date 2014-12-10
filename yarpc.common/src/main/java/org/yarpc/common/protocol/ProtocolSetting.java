package org.yarpc.common.protocol;


import java.nio.charset.Charset;

/**
 * Created by jingtian.zjt on 2014/12/4.
 */
public class ProtocolSetting {

    public static final int DEFAULT_MAX_IDLE = 3000;

    public static final int DEFAULT_TIMEOUT = 1000;

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
}
