package org.yarpc.common.protocol.rpc;

import org.yarpc.common.client.Client;
import org.yarpc.common.client.ClientHandler;
import org.yarpc.common.codec.Codecs;
import org.yarpc.common.domain.Metadata;
import org.yarpc.common.domain.RemoteRequest;
import org.yarpc.common.domain.RemoteResponse;
import org.yarpc.common.exception.ExceptionCode;
import org.yarpc.common.exception.RemoteException;
import org.yarpc.common.protocol.ProtocolSetting;
import org.yarpc.common.util.UUIDGenerator;


/**
 * Created by jingtian.zjt on 2014/12/7.
 */
public class RPCClientHandler implements ClientHandler<RPCRequest> {

    @Override
    public RemoteResponse handleInvoke(Client client, RemoteRequest request, Metadata metadata) throws RemoteException {
        byte[] targetBytes = request.getTarget().getBytes(ProtocolSetting.DEFAULT_CHARSET);
        byte[] methodBytes = request.getMethod().getBytes(ProtocolSetting.DEFAULT_CHARSET);
        byte[][] argTypeBytes = new byte[request.getArgs().length][];
        byte[][] argBytes = new byte[request.getArgs().length][];
        byte[] propertiesBytes = new byte[0];
        for (int i = 0; i < request.getArgs().length; i++) {
            argTypeBytes[i] = request.getArgTypes()[i].getBytes(ProtocolSetting.DEFAULT_CHARSET);
            try {
                argBytes[i] = Codecs.getEncoder(metadata.getCodecType()).encode(request.getArgs()[i]);
            } catch (Exception e) {
                throw new RemoteException(ExceptionCode.REMOTE_CODECS_ENCODE_FAILED, "[REMOTE] encode args failed.", e);
            }
        }
        RPCRequest rpcRequest = new RPCRequest(UUIDGenerator.get(), metadata.getTimeout(),
                targetBytes, methodBytes, argTypeBytes,argBytes, propertiesBytes, (byte)metadata.getCodecType());
        RPCResponse rpcResponse = (RPCResponse) client.syncInvoke(rpcRequest);
        return buildResponse(rpcResponse);
    }

    public RemoteResponse buildResponse(RPCResponse response) throws RemoteException{
        RemoteResponse remoteResponse = new RemoteResponse();
        switch (response.getStatus()) {
            case OK:
                try {
                    Object data = Codecs.getDecoder(response.getCodecType()).decode(response.getResponse());
                    remoteResponse.setData(data);
                } catch (Exception e) {
                    throw new RemoteException(ExceptionCode.REMOTE_CODECS_DECODE_FAILED, "[REMOTE] decode return value failed.", e);
                }
                break;
            case ERROR:
            default:
                throw new RemoteException(ExceptionCode.REMOTE_SERVER_INVOKE_FAILED, new String(response.getResponse(), ProtocolSetting.DEFAULT_CHARSET));
        }
        return remoteResponse;
    }
}
