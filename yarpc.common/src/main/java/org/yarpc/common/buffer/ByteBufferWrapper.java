package org.yarpc.common.buffer;

/**
 * Created by jingtian.zjt on 2014/12/3.
 */
public interface ByteBufferWrapper {

    public void init(int capacity);

    public void writeByte(byte data);

    public void writeBytes(byte[] data);

    public Byte readByte();

    public void readBytes(byte[] data);

    public void writeInt(int data);

    public int readInt();

    public long readLong();

    public void writeLong(long data);

    public int readerIndex();

    public void setReaderIndex(int index);

    public int readableBytes();



}
