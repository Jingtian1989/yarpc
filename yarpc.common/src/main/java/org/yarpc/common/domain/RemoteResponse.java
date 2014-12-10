package org.yarpc.common.domain;

/**
 * Created by jingtian.zjt on 2014/12/5.
 */
public class RemoteResponse {

    private Object data;
    private String error;

    public boolean isError() {
        return error == null ? false : true;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
