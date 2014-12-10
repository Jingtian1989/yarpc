package org.yarpc.netty.buffer;

import org.yarpc.common.buffer.ByteBufferWrapper;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;


/**
 * Created by jingtian.zjt on 2014/12/3.
 */
public class NettyByteBufferWrapper implements ByteBufferWrapper {

    private ChannelBuffer buffer;
    private Channel channel;

    public NettyByteBufferWrapper(){

    }

    public NettyByteBufferWrapper(ChannelBuffer buffer, Channel channel) {
        this.buffer = buffer;
        this.channel = channel;
    }

    @Override
    public void init(int capacity) {
        buffer = ChannelBuffers.dynamicBuffer(capacity);
    }

    @Override
    public void writeByte(byte data) {
        buffer.writeByte(data);
    }

    @Override
    public void writeBytes(byte[] data) {
        buffer.writeBytes(data);
    }

    @Override
    public Byte readByte() {
        return buffer.readByte();
    }

    @Override
    public void readBytes(byte[] data) {
        buffer.readBytes(data);
    }

    @Override
    public void writeInt(int data) {
        buffer.writeInt(data);
    }

    @Override
    public int readInt() {
        return buffer.readInt();
    }

    @Override
    public long readLong() {
        return buffer.readLong();
    }

    @Override
    public void writeLong(long data) {
        buffer.writeLong(data);
    }

    @Override
    public int readerIndex() {
        return buffer.readerIndex();
    }

    @Override
    public void setReaderIndex(int readerIndex) {
        buffer.setIndex(readerIndex, buffer.writerIndex());
    }

    @Override
    public int readableBytes() {
        return buffer.readableBytes();
    }


    public ChannelBuffer getBuffer() {
        return buffer;
    }
}
