package com.aegps.location.api.tool;

import com.aegps.location.api.module.SysDataTableItem;
import com.aegps.location.api.network.Callback;
import com.aegps.location.api.network.SoapClient;
import com.aegps.location.api.network.SoapRequest;

import org.ksoap2.SoapEnvelope;

/**
 * Created by shenhe on 2017/3/6.
 */

public class SoapUtil {
    private static final String TAG = "SoapUtil";
    private static SoapUtil mInstance;
    private SoapClient mSoapClient;

    public static final String mWeatherEndPoint = "http://182.92.191.17:8800/TradingService.svc";

    public static final String mNameSpace = "http://182.92.191.17:8800/";
    public int mSOAPVersion = SoapEnvelope.VER11;

    private SoapUtil() {
        mSoapClient = new SoapClient();
        //设置是否是调试模式
        mSoapClient.setDebug(true);
    }

    public static synchronized SoapUtil getInstance() {
        if (mInstance == null) {
            mInstance = new SoapUtil();
        }
        return mInstance;
    }


    /**
     * 异步调用
     * 获取账套信息
     *
     * @param methodName
     * @param callback
     */
    public void getAccountData(String methodName, SysDataTableItem sysDataTableItem, Callback callback) {
        SoapRequest request = new SoapRequest.Builder().endPoint(mWeatherEndPoint)
                .methodName(methodName)
                .soapAction(mNameSpace + methodName)
                .addParam("SysDataTable", sysDataTableItem)
                .nameSpace(mNameSpace)
                .setVersion(mSOAPVersion)
                .setDotNet(true)
                .build();
        mSoapClient.newCall(request).enqueue(callback);
    }

    /**
     * 同步调用示例
     *
     * @param cityName
     * @return
     */
    public SoapEnvelope getAccountData(String cityName) {
        SoapRequest request = new SoapRequest.Builder().endPoint(mWeatherEndPoint)
                .methodName("getAccountData")
                .soapAction(mNameSpace + "getAccountData")
                .addParam("byProvinceName", cityName)
                .nameSpace(mNameSpace)
                .setVersion(mSOAPVersion)
                .setDotNet(true)
                .build();
        return mSoapClient.newCall(request).execute();
    }

}
