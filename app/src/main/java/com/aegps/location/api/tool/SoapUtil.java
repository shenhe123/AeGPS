package com.aegps.location.api.tool;

import com.aegps.location.api.module.SysDataTableList;
import com.aegps.location.api.network.Callback;
import com.aegps.location.api.network.SoapClient;
import com.aegps.location.api.network.SoapRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.ksoap2.SoapEnvelope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shenhe on 2017/3/6.
 */

public class SoapUtil {
    private static final String TAG = "SoapUtil";
    private static SoapUtil mInstance;
    private SoapClient mSoapClient;

    public static final String mWeatherEndPoint = "http://182.92.191.17:8800/TradingService.svc";
    public static final String soapAction = "http://tempuri.org/ITradingService/GetJsonData";
    public static final String mNameSpace = "http://tempuri.org/";
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
    public void getAccountData(String methodName, SysDataTableList.SysDataTable sysDataTable, Callback callback) {
        SysDataTableList sysDataTableList = new SysDataTableList();
        List<SysDataTableList.SysDataTable> sysDataTables = new ArrayList<>();
        sysDataTables.add(sysDataTable);
        sysDataTableList.setData(sysDataTables);
        SoapRequest request = new SoapRequest.Builder().endPoint(mWeatherEndPoint)
                .methodName(methodName)
                .soapAction(soapAction)
                .addParam("sJsonInData", new Gson().toJson(sysDataTableList))
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