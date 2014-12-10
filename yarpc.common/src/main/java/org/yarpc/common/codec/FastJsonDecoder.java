package org.yarpc.common.codec;

import com.alibaba.fastjson.JSON;
import org.yarpc.common.protocol.ProtocolSetting;

/**
 * Created by jingtian.zjt on 2014/12/5.
 */
public class FastJsonDecoder implements Decoder {
    @Override
    public Object decode(byte[] bytes) throws Exception {
        return JSON.parse(new String(bytes, ProtocolSetting.DEFAULT_CHARSET));
    }
}
