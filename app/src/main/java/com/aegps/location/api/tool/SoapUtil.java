package com.aegps.location.api.tool;

import com.aegps.location.bean.SysDataTableList;
import com.aegps.location.api.network.Callback;
import com.aegps.location.api.network.SoapClient;
import com.aegps.location.api.network.SoapRequest;
import com.google.gson.Gson;

import org.ksoap2.SoapEnvelope;

import java.util.ArrayList;
import java.util.List;

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
    public static final String methodName = "GetJsonData";
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
     *
     * @param sysDataTable 必要参数
     * @param callback
     */
    public void getRequestData(SysDataTableList.SysDataTable sysDataTable, Callback callback) {
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
    public SoapEnvelope getRequestData(String cityName) {
        SoapRequest request = new SoapRequest.Builder().endPoint(mWeatherEndPoint)
                .methodName("getRequestData")
                .soapAction(mNameSpace + "getRequestData")
                .addParam("byProvinceName", cityName)
                .nameSpace(mNameSpace)
                .setVersion(mSOAPVersion)
                .setDotNet(true)
                .build();
        return mSoapClient.newCall(request).execute();
    }
}
