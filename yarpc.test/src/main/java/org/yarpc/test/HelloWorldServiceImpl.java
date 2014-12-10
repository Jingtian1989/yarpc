package org.yarpc.test;

/**
 * Created by jingtian.zjt on 2014/12/8.
 */
public class HelloWorldServiceImpl implements HelloWorldService {
    @Override
    public String sayHello(String name) {
        return "hello, " + name;
    }


    public void throwException2(String message) throws Exception {
        throw new Exception(message);
    }

    @Override
    public void throwException(String message) throws Exception{
        throwException2(message);
    }
}
