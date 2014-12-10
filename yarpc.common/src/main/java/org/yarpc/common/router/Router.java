package org.yarpc.common.router;

import org.yarpc.common.domain.Metadata;

import java.net.SocketAddress;

/**
 * Created by jingtian.zjt on 2014/12/7.
 */
public interface Router {

    public SocketAddress route(Metadata metadata);
}
