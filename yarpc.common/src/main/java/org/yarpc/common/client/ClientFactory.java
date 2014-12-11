package org.yarpc.common.client;

import org.yarpc.common.exception.YarpcException;

import java.net.SocketAddress;

/**
 * Created by jingtian.zjt on 2014/12/7.
 */
public interface ClientFactory {

    public Client get(SocketAddress address, boolean connect) throws YarpcException;

}
