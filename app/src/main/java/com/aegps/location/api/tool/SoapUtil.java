package com.aegps.location.api.tool;

import com.aegps.location.api.network.Callback;
import com.aegps.location.api.network.SoapClient;
import com.aegps.location.api.network.SoapRequest;
import com.aegps.location.bean.net.CommonReturnInfoTable;
import com.aegps.location.utils.toast.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
    private static Gson mGson;
    private SoapClient mSoapClient;

    public static final String mRemoteLoginUrl = "http://cloud.bjosoft.com:8999/CoordinateService.svc";
    public static final String mRemoteAction = "http://tempuri.org/ICoordinateService/GetJsonData";
    public static String mDomainUrl = "http://182.92.191.17:8800/TradingService.svc";
    public static String soapAction = "http://tempuri.org/ITradingService/GetJsonData";
//    public static String mDomainUrl = "";
//    public static String soapAction = "";
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

    public static synchronized Gson getGson() {
        if (mGson == null) {
            mGson = new GsonBuilder()
                    .serializeNulls()//允许序列化反序列化为null
                    .setLenient()
                    .create();
        }
        return mGson;
    }

    public static synchronized void onFailure(String message) {
        CommonReturnInfoTable commonReturnInfoTable = SoapUtil.getGson().fromJson(message, CommonReturnInfoTable.class);
        if (commonReturnInfoTable == null ||
                commonReturnInfoTable.getCommonReturnInfoTable() == null ||
                commonReturnInfoTable.getCommonReturnInfoTable().size() <= 0) return;
        ToastUtil.showShort(commonReturnInfoTable.getCommonReturnInfoTable().get(0).getExcpetionData());
    }

    /**
     * 获取账套信息
     *
     * @param callback
     */
    public void getDataBase(Callback callback) {
        HashMap<String, Object> param = new HashMap<>();
        param.put("sJsonInData", getDataSet("Plat_GetCountingRoomName", "", "", new HashMap<String, Object>()));
        SoapRequest request = new SoapRequest.Builder().endPoint(mDomainUrl)
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
        SoapRequest request = new SoapRequest.Builder().endPoint(mDomainUrl)
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
     * 请求远程登录地址
     *
     * @param callback
     */
    public void requestRemotelogin(Callback callback) {
        HashMap<String, Object> params = new HashMap<>();
        List<JsonObject> jsonObjects = new ArrayList<>();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("SoftProductCode", "07");
        jsonObject.addProperty("LogonUser", "");
        jsonObjects.add(jsonObject);
        params.put("InfoTable", jsonObjects);
        String jsonParams = getDataSet("Plat_CloudCustomerInfo", "", "BJOSOFTRegisterDB", params);
        params.clear();
        params.put("sJsonInData", jsonParams);
        SoapRequest request = new SoapRequest.Builder().endPoint(mRemoteLoginUrl)
                .methodName(methodName)
                .soapAction(mRemoteAction)
                .setParams(params)
                .nameSpace(mNameSpace)
                .setVersion(mSOAPVersion)
                .setDotNet(true)
                .build();
        mSoapClient.newCall(request).enqueue(callback);
    }

    /**
     * 远程登录
     *
     * @param cutomerCode 远程用户id
     * @param logonUser 远程用户名
     * @param callback
     */
    public void emotelogin(String cutomerCode, String logonUser, Callback callback) {
        HashMap<String, Object> params = new HashMap<>();
        List<JsonObject> jsonObjects = new ArrayList<>();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("CustomerCode", cutomerCode);
        jsonObject.addProperty("LogonUser", logonUser);
        jsonObjects.add(jsonObject);
        params.put("InfoTable", jsonObjects);
        String jsonParams = getDataSet("Plat_CloudBooksInfo", "", "BJOSOFTRegisterDB", params);
        params.clear();
        params.put("sJsonInData", jsonParams);
        SoapRequest request = new SoapRequest.Builder().endPoint(mRemoteLoginUrl)
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
        SoapRequest request = new SoapRequest.Builder().endPoint(mDomainUrl)
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
     * 刷新监控
     * @param mobileId 用户id
     * @param dataName 账套名
     * @param callback
     */
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
        SoapRequest request = new SoapRequest.Builder().endPoint(mDomainUrl)
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
     * 载货启动
     * @param mobileId 用户id
     * @param dataName 账套名
     * @param trafficBarCode 运输单code
     * @param callback
     */
    public void loadBegin(String mobileId, String dataName, String trafficBarCode, Callback callback) {
        HashMap<String, Object> params = new HashMap<>();
        List<JsonObject> jsonObjects = new ArrayList<>();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MobileID", mobileId);
        jsonObject.addProperty("TrafficBarCode", trafficBarCode);
        jsonObjects.add(jsonObject);
        params.put("InfoTable", jsonObjects);
        String jsonParams = getDataSet("LO_MobileTraffic_CargoStart", "", dataName, params);
        params.clear();
        params.put("sJsonInData", jsonParams);
        SoapRequest request = new SoapRequest.Builder().endPoint(mDomainUrl)
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
     * ic_unload_receipt
     * @param mobileId 用户id
     * @param dataName 账套名
     * @param shipmentBarCode 运输单code
     * @param callback
     */
    public void unloadReceipt(String mobileId, String dataName, String shipmentBarCode, Callback callback) {
        HashMap<String, Object> params = new HashMap<>();
        List<JsonObject> jsonObjects = new ArrayList<>();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MobileID", mobileId);
        jsonObject.addProperty("ShipmentBarCode", shipmentBarCode);
        jsonObjects.add(jsonObject);
        params.put("InfoTable", jsonObjects);
        String jsonParams = getDataSet("LO_MobileTraffic_UnloadSigning", "", dataName, params);
        params.clear();
        params.put("sJsonInData", jsonParams);
        SoapRequest request = new SoapRequest.Builder().endPoint(mDomainUrl)
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
     * 变更运输
     * @param mobileId 用户id
     * @param dataName 账套名
     * @param trafficBarCode 运输单code
     * @param callback
     */
    public void changeCarriage(String mobileId, String dataName, String trafficBarCode, Callback callback) {
        HashMap<String, Object> params = new HashMap<>();
        List<JsonObject> jsonObjects = new ArrayList<>();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MobileID", mobileId);
        jsonObject.addProperty("TrafficBarCode", trafficBarCode);
        jsonObjects.add(jsonObject);
        params.put("InfoTable", jsonObjects);
        String jsonParams = getDataSet("LO_MobileTraffic_ChangeCarriage", "", dataName, params);
        params.clear();
        params.put("sJsonInData", jsonParams);
        SoapRequest request = new SoapRequest.Builder().endPoint(mDomainUrl)
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
     * 位置上报
     * @param mobileId 用户id
     * @param dataName 账套名
     * @param longitudeX 经度
     * @param latitudeY 纬度
     * @param callback
     */
    public void locationTargeting(String mobileId, String dataName, String longitudeX, String latitudeY, Callback callback) {
        HashMap<String, Object> params = new HashMap<>();
        List<JsonObject> jsonObjects = new ArrayList<>();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MobileID", mobileId);
        jsonObject.addProperty("LongitudeX", longitudeX);
        jsonObject.addProperty("LatitudeY", latitudeY);
        jsonObjects.add(jsonObject);
        params.put("InfoTable", jsonObjects);
        String jsonParams = getDataSet("LO_MobileTraffic_LocationTargeting", "", dataName, params);
        params.clear();
        params.put("sJsonInData", jsonParams);
        SoapRequest request = new SoapRequest.Builder().endPoint(mDomainUrl)
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
