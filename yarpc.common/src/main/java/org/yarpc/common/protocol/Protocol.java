package org.yarpc.common.protocol;

import org.yarpc.common.buffer.ByteBufferWrapper;

/**
 * Created by jingtian.zjt on 2014/12/3.
 */
public interface Protocol {

    public void encode(BaseHeader message, ByteBufferWrapper wrapper) throws Exception;

    public BaseHeader decode(ByteBufferWrapper wrapper, int origin) throws Exception;

}
