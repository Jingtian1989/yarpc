package org.yarpc.common.domain;

import org.yarpc.common.codec.Codecs;
import org.yarpc.common.protocol.ProtocolSetting;
import org.yarpc.common.protocol.rpc.RPCProtocol;

/**
 * Created by jingtian.zjt on 2014/12/6.
 */
public class Metadata {

    private String name = "";
    private String ifClass = "";
    private Class<?> proxyClazz = null;
    private ClassLoader classLoader = null;
    private Object target = null;
    private int codecType = Codecs.JAVA_CODEC;
    private int protocol = RPCProtocol.RPC_PROTOCOL;
    private int timeout = ProtocolSetting.DEFAULT_TIMEOUT;


    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public String getIfClass() {
        return ifClass;
    }

    public void setIfClass(String ifClass) {
        this.ifClass = ifClass;
    }

    public int getCodecType() {
        return codecType;
    }

    public void setCodecType(int codecType) {
        this.codecType = codecType;
    }

    public void setProxyClass(Class<?> clazz) {
        this.proxyClazz = clazz;
        this.classLoader = proxyClazz.getClassLoader();
    }

    public Class<?> getProxyClazz() {
        return proxyClazz;
    }

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Object getTarget(){
        return target;
    }

}
