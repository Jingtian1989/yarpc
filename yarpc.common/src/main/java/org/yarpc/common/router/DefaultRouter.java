package org.yarpc.common.router;

import org.yarpc.common.domain.Metadata;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Created by jingtian.zjt on 2014/12/9.
 */
public class DefaultRouter implements Router {

    private String host;
    private int port;
    public DefaultRouter(String host, int port) {
       this.host = host;
       this.port = port;
    }
    @Override
    public SocketAddress route(Metadata metadata) {
        return new InetSocketAddress(host, port);
    }
}
