package com.aegps.location.locationservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.aegps.location.AeApplication;
import com.aegps.location.MainActivity;
import com.aegps.location.api.network.Callback;
import com.aegps.location.api.tool.SoapUtil;
import com.aegps.location.utils.ApplicationUtil;
import com.aegps.location.utils.FilteWriterUtil;
import com.aegps.location.utils.LogUtil;
import com.aegps.location.utils.SharedPrefUtils;
import com.aegps.location.utils.ThreadManager;
import com.aegps.location.utils.toast.ToastUtil;
import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LocationChangBroadcastReceiver extends BroadcastReceiver {

    public static final String RECEIVER_ACTION = "location_in_background";
    public static final String RECEIVER_DATA = "location_data";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(RECEIVER_ACTION)) {
            AMapLocation location = (AMapLocation) intent.getParcelableExtra(RECEIVER_DATA);
            if (null != location) {
                double[] gcj02tobd09 = CoordinateTransformUtil.gcj02tobd09(location.getLongitude(), location.getLatitude());
                if (Math.abs(Math.floor(gcj02tobd09[1])) == 0 && Math.abs(Math.floor(gcj02tobd09[0])) == 0) return;
                LatLng locationLatLng = new LatLng(gcj02tobd09[1], gcj02tobd09[0]);
                Log.e("shenhe 定位結果", "LocationChangBroadcastReceiver: " + locationLatLng);
                SharedPrefUtils.saveString("locationLatLng", locationLatLng.latitude + "," + locationLatLng.longitude);
                PowerManagerUtil.acquireWakeLock(context);
//                FilteWriterUtil.wirteToLoacal(FilteWriterUtil.getRootDir(AeApplication.getAppContext()) + "/1/log.txt"
//                        , "当前时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
//                                + "\nlongitude=" + locationLatLng.longitude
//                                + "\nlatitude=" + locationLatLng.latitude
//                                + "\ncountry=" + location.getCountry()
//                                + "\ncity=" + location.getCity()
//                                + "\nstreet=" + location.getStreet()
//                                + "\naddress=" + location.getAddress() + "\n\n");
                processCustomMessageA(context, location);
            }
        }
    }

    //send msg to MainActivity
    private void processCustomMessageA(Context context, AMapLocation aMapLocation) {
        if (MainActivity.isForeground) {
            Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
            if (aMapLocation != null) {
                msgIntent.putExtra("data", aMapLocation);
            }
            context.sendBroadcast(msgIntent);
        }
    }
}
