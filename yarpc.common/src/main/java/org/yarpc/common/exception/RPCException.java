package org.yarpc.common.exception;


/**
 * Created by jingtian.zjt on 2014/12/7.
 */
public class RPCException extends Exception {

    private RPCCode code;
    public RPCException(RPCCode code, String s, Throwable e) {
        super(s, e);
        this.code = code;
    }

    public RPCException(RPCCode code, String s) {
        super(s);
        this.code = code;
    }

    public RPCCode getCode() {
        return code;
    }
}
