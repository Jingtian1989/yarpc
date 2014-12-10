package org.yarpc.common.codec;

/**
 * Created by jingtian.zjt on 2014/12/5.
 */
public interface Decoder {

    public Object decode(byte[] bytes) throws Exception;
}
