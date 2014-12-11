package org.yarpc.common.exception;


/**
 * Created by jingtian.zjt on 2014/12/7.
 */
public class YarpcException extends Exception {

    private YarpcCode code;
    public YarpcException(YarpcCode code, String s, Throwable e) {
        super(s, e);
        this.code = code;
    }

    public YarpcException(YarpcCode code, String s) {
        super(s);
        this.code = code;
    }

    public YarpcCode getCode() {
        return code;
    }
}
