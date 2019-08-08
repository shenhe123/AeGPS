package com.aegps.location.api.tool;

import com.aegps.location.bean.SysDataTableList;
import com.aegps.location.api.network.Callback;
import com.aegps.location.api.network.SoapClient;
import com.aegps.location.api.network.SoapRequest;
import com.aegps.location.utils.ApplicationUtil;
import com.aegps.location.utils.LogUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

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
     * 获取账套信息
     *
     * @param callback
     */
    public void getDataBase(Callback callback) {
        HashMap<String, Object> param = new HashMap<>();
        param.put("sJsonInData", getDataSet("Plat_GetCountingRoomName", "", "", new HashMap<String, Object>()));
        SoapRequest request = new SoapRequest.Builder().endPoint(mWeatherEndPoint)
                .methodName(methodName)
                .soapAction(soapAction)
                .setParams(param)
                .nameSpace(mNameSpace)
                .setVersion(mSOAPVersion)
                .setDotNet(true)
                .build();
        mSoapClient.newCall(request).enqueue(callback);
    }

    /**
     * 获取手机默认关联车牌号
     *
     * @param dataName 账套名
     * @param userCode 用户id
     * @param callback
     */
    public void getMobileVehicle(String dataName, String userCode, Callback callback) {
        HashMap<String, Object> params = new HashMap<>();
        List<JsonObject> jsonObjects = new ArrayList<>();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MobileID", userCode);
        jsonObjects.add(jsonObject);
        params.put("InfoTable", jsonObjects);
        String jsonParams = getDataSet("LO_MobileTraffic_GetMobileVehicle", "", dataName, params);
        params.clear();
        params.put("sJsonInData", jsonParams);
        SoapRequest request = new SoapRequest.Builder().endPoint(mWeatherEndPoint)
                .methodName(methodName)
                .soapAction(soapAction)
                .setParams(params)
                .nameSpace(mNameSpace)
                .setVersion(mSOAPVersion)
                .setDotNet(true)
                .build();
        mSoapClient.newCall(request).enqueue(callback);
    }

    /**
     * 登录
     * @param userCode 用户id
     * @param password 密码  车牌号
     * @param dataName 账套名
     * @param callback
     */
    public void login(String userCode, String password, String dataName, Callback callback) {
        HashMap<String, Object> params = new HashMap<>();
        List<JsonObject> jsonObjects = new ArrayList<>();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("UserCode", userCode);
        jsonObject.addProperty("Password", password);
        jsonObjects.add(jsonObject);
        params.put("InfoTable", jsonObjects);
        String jsonParams = getDataSet("Plat_Login", "", dataName, params);
        params.clear();
        params.put("sJsonInData", jsonParams);
        SoapRequest request = new SoapRequest.Builder().endPoint(mWeatherEndPoint)
                .methodName(methodName)
                .soapAction(soapAction)
                .setParams(params)
                .nameSpace(mNameSpace)
                .setVersion(mSOAPVersion)
                .setDotNet(true)
                .build();
        mSoapClient.newCall(request).enqueue(callback);
    }

    public void refreshMonitor(String mobileId, String dataName, Callback callback) {
        HashMap<String, Object> params = new HashMap<>();
        List<JsonObject> jsonObjects = new ArrayList<>();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MobileID", mobileId);
        jsonObjects.add(jsonObject);
        params.put("InfoTable", jsonObjects);
        String jsonParams = getDataSet("LO_MobileTraffic_RefreshMonitor", "", dataName, params);
        params.clear();
        params.put("sJsonInData", jsonParams);
        SoapRequest request = new SoapRequest.Builder().endPoint(mWeatherEndPoint)
                .methodName(methodName)
                .soapAction(soapAction)
                .setParams(params)
                .nameSpace(mNameSpace)
                .setVersion(mSOAPVersion)
                .setDotNet(true)
                .build();
        mSoapClient.newCall(request).enqueue(callback);
    }

    private static String getDataSet(String handleCode, String userCode, String dataName, Map<String, Object> params){
        Map<String, Object> dataSets = new HashMap<>();
        List<JsonObject> sysDataTable = new ArrayList<>();
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("HandleType", "0");
            jsonObject.addProperty("HandleCode", handleCode);
            jsonObject.addProperty("UserCode", userCode);
            jsonObject.addProperty("PlatCode", "07");
            jsonObject.addProperty("DataBaseName", dataName);
            jsonObject.addProperty("ClientIP", "");
            jsonObject.addProperty("FunctionID", "");
            sysDataTable.add(jsonObject);
            dataSets.put("SysDataTable", sysDataTable);
            for(String key : params.keySet()){
                dataSets.put(key, params.get(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String paramStr = new Gson().toJson(dataSets);
        System.out.println(paramStr);
        return paramStr;
    }
}
