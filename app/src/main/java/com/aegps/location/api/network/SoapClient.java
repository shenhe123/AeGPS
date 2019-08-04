package com.aegps.location.api.network;

/**
 * Created by shenhe on 2017/3/6.
 */

public final class SoapClient {
    private Dispatcher mDispatcher;
    private boolean isDebug;

    public SoapClient() {
        mDispatcher = new Dispatcher();
    }

    public Dispatcher getDispatcher() {
        return mDispatcher;
    }

    public SoapCall newCall(SoapRequest request) {
        return new SoapCall(request, this);
    }

    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }
}
