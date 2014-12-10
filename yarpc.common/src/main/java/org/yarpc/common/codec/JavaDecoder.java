package org.yarpc.common.codec;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

/**
 * Created by jingtian.zjt on 2014/12/5.
 */
public class JavaDecoder implements Decoder {

    @Override
    public Object decode(byte[] bytes) throws Exception {
        ByteArrayInputStream array = new ByteArrayInputStream(bytes);
        ObjectInputStream input = new ObjectInputStream(array);
        Object object = input.readObject();
        return object;
    }
}
