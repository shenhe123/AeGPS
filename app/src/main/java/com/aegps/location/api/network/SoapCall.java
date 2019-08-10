package com.aegps.location.api.network;

import android.text.TextUtils;

import com.aegps.location.utils.LogUtil;
import com.google.gson.Gson;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;

/**
 * Created by shenhe on 2017/3/6.
 */

public class SoapCall implements Call {

    private SoapRequest mSoapRequest;
    private Dispatcher mDispatcher;
    private SoapClient mSoapClient;

    private SoapHttpEngine mSoapHttpEngine;
    private boolean executed = false;


    public SoapCall(SoapRequest soapRequest, SoapClient client) {
        try {
            mSoapRequest = soapRequest;
            mSoapClient = client;
            mDispatcher = client.getDispatcher();
            mSoapHttpEngine = new SoapHttpEngine(soapRequest);
            mSoapHttpEngine.setDebug(mSoapClient.isDebug());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public SoapEnvelope execute() {
        synchronized (this) {
            if (executed) throw new IllegalStateException("Already Executed");
            executed = true;
        }
        SoapEnvelope result = null;
        try {
            mDispatcher.executed(this);
            result = mSoapHttpEngine.doPost();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDispatcher.finished(this);
        }
        return result;
    }

    @Override
    public void enqueue(Callback callback) {
        synchronized (this) {
            if (executed) throw new IllegalStateException("Already Executed");
            executed = true;
        }
        mDispatcher.enqueue(new AsyncCall(callback));
    }

    final class AsyncCall implements Runnable {
        private final Callback callback;

        AsyncCall(Callback callback) {
            this.callback = callback;
        }

        SoapRequest request() {
            return mSoapRequest;
        }

        @Override
        public void run() {
            try {
                SoapHttpEngine engine = new SoapHttpEngine(mSoapRequest);
                SoapEnvelope respose = engine.doPost();
                // 获取返回的数据
                SoapObject object = (SoapObject) respose.bodyIn;
                if(null==object){
                    return;
                }
                LogUtil.d("envelope.bodyIn:--->" + respose.bodyIn.toString());
                // 获取返回的结果
                String result = object.getProperty(0).toString();
                String data = object.getProperty(1).toString();
                LogUtil.d("result:--->" + result);
                LogUtil.d("data:--->" + data);
                callback.onResponse(TextUtils.equals("true", result), data);
            } catch (Exception e) {
                callback.onFailure(e.getMessage());
                e.printStackTrace();
            } finally {
                mDispatcher.finished(this);
            }
        }
    }

    public boolean isExecuted() {
        return executed;
    }
}
