package org.yarpc.common.codec;

/**
 * Created by jingtian.zjt on 2014/12/5.
 */
public interface Encoder {

    public byte[] encode(Object object) throws Exception;
}
