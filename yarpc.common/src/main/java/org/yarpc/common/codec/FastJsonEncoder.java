package org.yarpc.common.codec;

import com.alibaba.fastjson.JSON;
import org.yarpc.common.protocol.ProtocolSetting;

/**
 * Created by jingtian.zjt on 2014/12/5.
 */
public class FastJsonEncoder implements Encoder{

    @Override
    public byte[] encode(Object object) {
        String text = JSON.toJSONString(object);
        return text.getBytes(ProtocolSetting.DEFAULT_CHARSET);
    }
}
