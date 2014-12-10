package org.yarpc.common.protocol;

import org.yarpc.common.client.ClientHandler;
import org.yarpc.common.protocol.rpc.RPCClientHandler;
import org.yarpc.common.protocol.rpc.RPCProtocol;
import org.yarpc.common.protocol.rpc.RPCServerHandler;
import org.yarpc.common.server.ServerHandler;

/**
 * Created by jingtian.zjt on 2014/12/3.
 */
public final class ProtocolFactory {

    private static final Protocol[] protocols = new Protocol[256];
    private static final ServerHandler<?>[] serverHandlers = new ServerHandler<?>[256];
    private static final ClientHandler<?>[] clientHandlers = new ClientHandler<?>[256];
    private static final ProtocolFactory instance = new ProtocolFactory();
    public static ProtocolFactory getInstance() {
        return instance;
    }

    private ProtocolFactory(){}

    static {
        registerProtocol(RPCProtocol.RPC_PROTOCOL, new RPCProtocol(), new RPCServerHandler(), new RPCClientHandler());
    }

    public static void registerProtocol(byte type, Protocol protocol, ServerHandler<?> serverHandler, ClientHandler<?> clientHandler) {
        type = (type < 0 ? type += 128 : type);
        if (protocols[type] != null) {
            throw new RuntimeException("protocol handler has been registered.");
        }
        protocols[type] = protocol;
        serverHandlers[type] = serverHandler;
        clientHandlers[type] = clientHandler;
    }

    public Protocol getProtocol(int type) {
        type = (type < 0 ? type + 128 : type);
        return protocols[type];
    }

    public ServerHandler<?> getServerHandler(int type) {
        type = (type < 0 ? type + 128 : type);
        return serverHandlers[type];
    }

    public ClientHandler<?> getClientHandler(int type) {
        if (type < 0) {
            type += 128;
        }
        return clientHandlers[type];
    }




}
