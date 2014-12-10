package org.yarpc.common.protocol.rpc;

import org.yarpc.common.buffer.ByteBufferWrapper;
import org.yarpc.common.protocol.BaseHeader;
import org.yarpc.common.protocol.Protocol;
import org.yarpc.common.protocol.ProtocolStatus;

/**
 * Created by jingtian.zjt on 2014/12/3.
 */

/*
 * Common RPC protocol.
 * 请求：
 * 字节数       描述
 * 1           协议
 * 1           版本
 * 1           请求
 * 1         序列化方式
 * 4          保留字节
 * 8          请求ID
 * 4          超时时间
 * 4          服务名长度
 * 4         方法名长度
 * 4         方法参数个数
 * 4          附加信息长度
 * 4*参数个数 各个参数类型的长度
 * 4*参数个数 各个参数值的长度
 * 不等      各个参数类型的值
 * 不等      各个参数值
 * 不等      服务名，方法名
 * 不等        附加信息值
 *
 * 响应：
 * 字节数      描述
 * 1          协议
 * 1          版本
 * 1          响应
 * 1        序列化方式
 * 1        状态code
 * 3        保留字节
 * 8        对应的请求ID
 * 4        返回值的长度
 * 不等      返回值的值
 */
public class RPCProtocol implements Protocol {

    public static final byte RPC_PROTOCOL = 0x5f;

    private static final int REQUEST_HEADER_LEN = 36;
    private static final int RESPONSE_HEADER_LEN = 20;

    private static final byte VERSION = (byte)0x01;
    private static final byte REQUEST = (byte)0x00;
    private static final byte RESPONSE = (byte)0x01;

    @Override
    public void encode(BaseHeader message, ByteBufferWrapper wrapper) throws Exception {
        if (message instanceof RPCRequest) {
            encodeRequest((RPCRequest) message, wrapper);
        } else if (message instanceof  RPCResponse) {
            encodeResponse((RPCResponse) message, wrapper);
        } else {
            throw new IllegalArgumentException("unsupported message type");
        }
    }

    @Override
    public BaseHeader decode(ByteBufferWrapper wrapper, int origin) throws Exception {
        //校验版本
        if (wrapper.readableBytes() < 2) {
            wrapper.setReaderIndex(origin);
            return null;
        }
        byte version = wrapper.readByte();
        if (version == VERSION) {
            byte type = wrapper.readByte();
            if (type == REQUEST) {
                return decodeRequest(wrapper, origin);
            } else if (type == RESPONSE) {
                return decodeResponse(wrapper, origin);
            } else {
                throw new RuntimeException("protocol type " + type + " is not supported");
            }
        } else {
            throw new RuntimeException("protocol version " + version + "is not supported");
        }
    }


    private void encodeRequest(RPCRequest request, ByteBufferWrapper wrapper) {
        int argTypesLen = 0;
        int argsLen = 0;
        if (request.getArgTypes() != null) {
            for (byte[] argType : request.getArgTypes()) {
                argTypesLen += argType.length;
            }
        }
        if (request.getArgs() != null) {
            for (byte[] arg: request.getArgs()) {
                argsLen += arg.length;
            }
        }

        int argCount = (request.getArgs() != null) ? request.getArgs().length : 0;
        int propertiesLen = (request.getProperties() != null) ? request.getProperties().length : 0;

        int capacity = REQUEST_HEADER_LEN + argCount * 4 * 2 + argTypesLen + request.getTarget().length +
                request.getMethod().length + argsLen + propertiesLen;
        //请求头信息
        wrapper.init(capacity);
        wrapper.writeByte(RPCProtocol.RPC_PROTOCOL);
        wrapper.writeByte(VERSION);
        wrapper.writeByte(REQUEST);
        wrapper.writeByte(request.getCodecType());
        wrapper.writeInt(0);
        wrapper.writeLong(request.getRequestID());
        wrapper.writeInt(request.getTimeout());
        wrapper.writeInt(request.getTarget().length);
        wrapper.writeInt(request.getMethod().length);
        wrapper.writeInt(argCount);
        wrapper.writeInt(propertiesLen);

        //各个参数的类型的长度
        if (request.getArgTypes() != null) {
            for (byte[] argType : request.getArgTypes()) {
                wrapper.writeInt(argType.length);
            }
        }

        //各个参数的值的长度
        if (request.getArgs() != null) {
            for (byte[] arg : request.getArgs()) {
                wrapper.writeInt(arg.length);
            }
        }

        //各个参数的类型
        if (request.getArgTypes() != null) {
            for (byte[] argType : request.getArgTypes()) {
                wrapper.writeBytes(argType);
            }
        }

        //各个参数值
        if (request.getArgs() != null) {
            for (byte[] arg : request.getArgs()) {
                wrapper.writeBytes(arg);
            }
        }

        //服务名，方法名
        wrapper.writeBytes(request.getTarget());
        wrapper.writeBytes(request.getMethod());

        //附加信息
        if (request.getProperties() != null) {
            wrapper.writeBytes(request.getProperties());
        }
    }

    private void encodeResponse(RPCResponse response, ByteBufferWrapper wrapper) {
        int capacity = RESPONSE_HEADER_LEN + response.getResponse().length;
        wrapper.init(capacity);
        wrapper.writeByte(RPCProtocol.RPC_PROTOCOL);
        wrapper.writeByte(VERSION);
        wrapper.writeByte(RESPONSE);
        wrapper.writeByte(response.getCodecType());
        wrapper.writeByte(response.getStatus().getStatusCode());
        wrapper.writeByte((byte)0);
        wrapper.writeByte((byte)0);
        wrapper.writeByte((byte)0);
        wrapper.writeLong(response.getRequestID());
        wrapper.writeInt(response.getResponse().length);
        wrapper.writeBytes(response.getResponse());
    }


    public BaseHeader decodeRequest(ByteBufferWrapper wrapper, int originPos) {

        //解析请求头
        if (wrapper.readableBytes() < REQUEST_HEADER_LEN - 3) {
            wrapper.setReaderIndex(originPos);
            return null;
        }
        byte codecType  = wrapper.readByte();
        int keep        = wrapper.readInt();
        long requestId  = wrapper.readLong();
        int timeout     = wrapper.readInt();
        int targetNameLen = wrapper.readInt();
        int methodNameLen = wrapper.readInt();
        int argCount    = wrapper.readInt();
        int propertiesLen = wrapper.readInt();

        //长度是否满足 各个参数类型的长度 +　各个参数值的长度
        int expectedLen = 4 * argCount * 2;
        if (wrapper.readableBytes() < expectedLen) {
            wrapper.setReaderIndex(originPos);
            return null;
        }
        expectedLen = 0;

        //解析各个参数类型的长度
        int[] argTypeLens = new int[argCount];
        for (int i = 0; i < argCount; i++) {
            argTypeLens[i] = wrapper.readInt();
            expectedLen += argTypeLens[i];
        }

        //解析各个参数值的长度
        int[] argLens = new int[argCount];
        for (int i = 0; i < argCount; i++) {
            argLens[i] = wrapper.readInt();
            expectedLen += argLens[i];
        }

        //长度是否满足 各个参数类型的值 + 服务名 + 方法名 + 方法参数值 + 附加信息
        expectedLen += targetNameLen + methodNameLen + propertiesLen;
        if (wrapper.readableBytes() < expectedLen) {
            wrapper.setReaderIndex(originPos);
            return null;
        }

        //解析各个参数类型的值
        byte[][] argTypes = new byte[argCount][];
        for (int i = 0; i < argCount; i++) {
            argTypes[i] = new byte[argTypeLens[i]];
            wrapper.readBytes(argTypes[i]);
        }

        //解析各个参数值
        byte[][] args = new byte[argCount][];
        for (int i = 0; i < argCount; i++) {
            args[i] = new byte[argLens[i]];
            wrapper.readBytes(args[i]);
        }

        //解析服务名，方法名，方法参数值
        byte[] targetName = new byte[targetNameLen];
        wrapper.readBytes(targetName);
        byte[] methodName = new byte[methodNameLen];
        wrapper.readBytes(methodName);
        byte[] properties = new byte[propertiesLen];
        wrapper.readBytes(properties);

        return new RPCRequest(requestId, timeout, targetName, methodName, argTypes, args, properties, codecType);
    }

    public BaseHeader decodeResponse(ByteBufferWrapper wrapper, int origin) {
        if (wrapper.readableBytes() < RESPONSE_HEADER_LEN - 3) {
            wrapper.setReaderIndex(origin);
            return null;
        }
        byte codecType = wrapper.readByte();
        byte status = wrapper.readByte();
        wrapper.readByte();
        wrapper.readByte();
        wrapper.readByte();
        long requestId = wrapper.readLong();
        int len = wrapper.readInt();
        if (wrapper.readableBytes() < len) {
            wrapper.setReaderIndex(origin);
            return null;
        }
        byte[] body = new byte[len];
        wrapper.readBytes(body);
        return new RPCResponse(requestId, codecType, ProtocolStatus.formatCode(status), body);
    }


}
