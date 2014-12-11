package org.yarpc.common.client;

import org.yarpc.common.exception.RPCException;
import org.yarpc.common.protocol.ProtocolSetting;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jingtian.zjt on 2014/12/7.
 */
public abstract class BaseClientFactory implements ClientFactory {

    private final ConcurrentHashMap<SocketAddress, Client> clients =
            new ConcurrentHashMap<SocketAddress, Client>();


    @Override
    public Client get(SocketAddress address, boolean connect) throws RPCException {
        Client client = clients.get(address);
        if (client == null && !connect) {
            return null;
        }
        if (client == null || !client.isConnected()) {
            synchronized (this) {
                client = clients.get(address);
                if (client == null) {
                    client = connect(address, ProtocolSetting.DEFAULT_TIMEOUT);
                    clients.put(address, client);
                    return client;
                } else {
                    clients.remove(address, client);
                    client = connect(address, ProtocolSetting.DEFAULT_TIMEOUT);
                    clients.put(address, client);
                    return client;
                }
            }
        } else {
            return client;
        }
    }

    public abstract Client connect(SocketAddress address, int timeout) throws RPCException;
}
