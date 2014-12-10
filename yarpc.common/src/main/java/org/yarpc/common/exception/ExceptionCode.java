package org.yarpc.common.exception;

/**
 * Created by jingtian.zjt on 2014/12/8.
 */
public enum ExceptionCode {

    YARPC_CODECS_DECODE_FAILED(0x00, "YARPC_CODECS_DECODE_ERROR"),
    YARPC_CODECS_ENCODE_FAILED(0x01, "YARPC_CODECS_ENCODE_ERROR"),
    YARPC_CLIENT_CONN_TIMEOUT(0x02, "YARPC_CLIENT_CONN_TIMEOUT"),
    YARPC_CLIENT_CONN_FAILED(0x03, "YARPC_CLIENT_CONN_FALIED"),
    YARPC_SERVER_INVOKE_FAILED(0x04, "YARPC_SERVER_PROC_FAILED");

    private int code;
    private String value;
    private ExceptionCode(int code, String value) {
        this.code = code;
        this.value = value;
    }
}
