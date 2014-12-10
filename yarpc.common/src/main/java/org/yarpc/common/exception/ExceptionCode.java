package org.yarpc.common.exception;

/**
 * Created by jingtian.zjt on 2014/12/8.
 */
public enum ExceptionCode {

    REMOTE_CODECS_DECODE_FAILED(0x00, "REMOTE_CODECS_DECODE_ERROR"),
    REMOTE_CODECS_ENCODE_FAILED(0x01, "REMOTE_CODECS_ENCODE_ERROR"),
    REMOTE_CLIENT_CONN_TIMEOUT(0X02, "REMOTE_CLIENT_CONN_TIMEOUT"),
    REMOTE_CLIENT_CONN_FAILED(0x03, "REMOTE_CLIENT_CONN_FALIED"),
    REMOTE_SERVER_INVOKE_FAILED(0x04, "REMOTE_SERVER_PROC_FAILED");

    private int code;
    private String value;
    private ExceptionCode(int code, String value) {
        this.code = code;
        this.value = value;
    }
}
