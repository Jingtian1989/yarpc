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
 * ����
 * �ֽ���       ����
 * 1           Э��
 * 1           �汾
 * 1           ����
 * 1         ���л���ʽ
 * 4          �����ֽ�
 * 8          ����ID
 * 4          ��ʱʱ��
 * 4          ����������
 * 4         ����������
 * 4         ������������
 * 4          ������Ϣ����
 * 4*�������� �����������͵ĳ���
 * 4*�������� ��������ֵ�ĳ���
 * ����      �����������͵�ֵ
 * ����      ��������ֵ
 * ����      ��������������
 * ����        ������Ϣֵ
 *
 * ��Ӧ��
 * �ֽ���      ����
 * 1          Э��
 * 1          �汾
 * 1          ��Ӧ
 * 1        ���л���ʽ
 * 1        ״̬code
 * 3        �����ֽ�
 * 8        ��Ӧ������ID
 * 4        ����ֵ�ĳ���
 * ����      ����ֵ��ֵ
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
        //У��汾
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
        //����ͷ��Ϣ
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

        //�������������͵ĳ���
        if (request.getArgTypes() != null) {
            for (byte[] argType : request.getArgTypes()) {
                wrapper.writeInt(argType.length);
            }
        }

        //����������ֵ�ĳ���
        if (request.getArgs() != null) {
            for (byte[] arg : request.getArgs()) {
                wrapper.writeInt(arg.length);
            }
        }

        //��������������
        if (request.getArgTypes() != null) {
            for (byte[] argType : request.getArgTypes()) {
                wrapper.writeBytes(argType);
            }
        }

        //��������ֵ
        if (request.getArgs() != null) {
            for (byte[] arg : request.getArgs()) {
                wrapper.writeBytes(arg);
            }
        }

        //��������������
        wrapper.writeBytes(request.getTarget());
        wrapper.writeBytes(request.getMethod());

        //������Ϣ
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

        //��������ͷ
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

        //�����Ƿ����� �����������͵ĳ��� +����������ֵ�ĳ���
        int expectedLen = 4 * argCount * 2;
        if (wrapper.readableBytes() < expectedLen) {
            wrapper.setReaderIndex(originPos);
            return null;
        }
        expectedLen = 0;

        //���������������͵ĳ���
        int[] argTypeLens = new int[argCount];
        for (int i = 0; i < argCount; i++) {
            argTypeLens[i] = wrapper.readInt();
            expectedLen += argTypeLens[i];
        }

        //������������ֵ�ĳ���
        int[] argLens = new int[argCount];
        for (int i = 0; i < argCount; i++) {
            argLens[i] = wrapper.readInt();
            expectedLen += argLens[i];
        }

        //�����Ƿ����� �����������͵�ֵ + ������ + ������ + ��������ֵ + ������Ϣ
        expectedLen += targetNameLen + methodNameLen + propertiesLen;
        if (wrapper.readableBytes() < expectedLen) {
            wrapper.setReaderIndex(originPos);
            return null;
        }

        //���������������͵�ֵ
        byte[][] argTypes = new byte[argCount][];
        for (int i = 0; i < argCount; i++) {
            argTypes[i] = new byte[argTypeLens[i]];
            wrapper.readBytes(argTypes[i]);
        }

        //������������ֵ
        byte[][] args = new byte[argCount][];
        for (int i = 0; i < argCount; i++) {
            args[i] = new byte[argLens[i]];
            wrapper.readBytes(args[i]);
        }

        //����������������������������ֵ
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
