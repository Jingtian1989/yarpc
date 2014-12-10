package org.yarpc.common.protocol;

/**
 * Created by jingtian.zjt on 2014/12/3.
 */
public enum ProtocolStatus {

    OK(0x10, "ok"),
    REFUSED(0x20, "refused"),
    ERROR(0x30, "error");

    private byte code;
    private String message;

    private ProtocolStatus(int code, String message) {
        this.code = (byte) code;
        this.message = message;
    }

    public byte getStatusCode() {
        return code;
    }

    public String getStatusMessage() {
        return message;
    }

    public static ProtocolStatus formatCode(int code) throws IllegalArgumentException{
        for(ProtocolStatus status : ProtocolStatus.values()) {
            if (status.getStatusCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("[REMOTE] unsupported protocol status code.");
    }

}
