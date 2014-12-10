package org.yarpc.test;

/**
 * Created by jingtian.zjt on 2014/12/8.
 */
public interface HelloWorldService {

    public String sayHello(String name);

    public void throwException(String message) throws Exception;
}

