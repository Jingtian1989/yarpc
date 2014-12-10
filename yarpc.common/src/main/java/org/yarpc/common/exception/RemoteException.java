package org.yarpc.common.exception;


/**
 * Created by jingtian.zjt on 2014/12/7.
 */
public class RemoteException extends Exception {

    private ExceptionCode code;
    public RemoteException(ExceptionCode code, String s, Throwable e) {
        super(s, e);
        this.code = code;
    }

    public RemoteException(ExceptionCode code, String s) {
        super(s);
        this.code = code;
    }

    public ExceptionCode getCode() {
        return code;
    }
}
