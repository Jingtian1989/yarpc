package org.yarpc.common.server;

/**
 * Created by jingtian.zjt on 2014/12/3.
 */
public abstract class BaseServer implements Server {

    protected String host;
    protected int port;
    public BaseServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void start() {
        startInternal();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run () {
                try {
                    BaseServer.this.refuseConnect();
                    Thread.sleep(10000);
                    BaseServer.this.stop();
                } catch (Exception e) {
                }
            }
        });
    }

    @Override
    public void stop() {
        stopInternal();
    }

    public abstract void refuseConnect();

    public abstract void openConnect();

    public abstract void startInternal();

    public abstract void stopInternal();
}
