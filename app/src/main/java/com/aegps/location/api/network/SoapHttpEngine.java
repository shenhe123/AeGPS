package com.aegps.location.api.network;

import com.aegps.location.utils.Contants;
import com.aegps.location.utils.LogUtil;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Map;

/**
 * Created by shenhe on 2017/3/6.
 */

public class SoapHttpEngine implements HttpEngine {

    private static final String TAG = "SoapHttpEngine";
    private SoapRequest mSoapRequest;
    private boolean isDebug = false;

    public SoapHttpEngine(SoapRequest soapRequest) {
        mSoapRequest = soapRequest;
    }

    @Override
    public SoapEnvelope doGet() throws Exception {
        return null;
    }

    @Override
    public SoapEnvelope doPost() throws Exception {
        //设置Soap对象
        SoapObject rpc = new SoapObject(mSoapRequest.getNameSpace(), mSoapRequest.getMethodName());
        //设置参数
        if (mSoapRequest.getParamsMap() != null) {
            for (Map.Entry<String, Object> e : mSoapRequest.getParamsMap().entrySet()) {
                rpc.addProperty(e.getKey(), e.getValue());
            }
        }
        for (int i = 0; i < rpc.getPropertyCount(); i++) {
            LogUtil.d("request params = " + rpc.getPropertyAsString(i));
        }
        //设置Soap消息
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(mSoapRequest.getVersion());
        envelope.bodyOut = rpc;
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = mSoapRequest.isDotNet();

        HttpTransportSE transport = new HttpTransportSE(mSoapRequest.getEndPoint());
        transport.debug = isDebug;

        transport.call(mSoapRequest.getSoapAction(), envelope);
//        if (envelope.getResponse() != null) {
//            SoapObject result = (SoapObject) envelope.bodyIn;
//            Log.e(TAG, result.toString());
//            Log.e(TAG, String.valueOf(result.getProperty("GetMp3Result")));
//        }
        return envelope;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }
}
