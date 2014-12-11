package org.yarpc.common.exception;

/**
 * Created by jingtian.zjt on 2014/12/8.
 */
public enum RPCCode {

    RPC_CODECS_DECODE_FAILED(0x00, "RPC_CODECS_DECODE_FAILED"),
    RPC_CODECS_ENCODE_FAILED(0x01, "RPC_CODECS_ENCODE_FAILED"),
    RPC_CLIENT_CONN_TIMEOUT(0x02, "RPC_CLIENT_CONN_TIMEOUT"),
    RPC_CLIENT_CONN_FAILED(0x03, "RPC_CLIENT_CONN_FAILED"),
    RPC_SERVER_INVOKE_FAILED(0x04, "RPC_SERVER_INVOKE_FAILED");

    private int code;
    private String value;
    private RPCCode(int code, String value) {
        this.code = code;
        this.value = value;
    }
}
