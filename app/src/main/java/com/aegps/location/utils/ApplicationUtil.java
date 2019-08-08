package com.aegps.location.utils;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.aegps.location.AeApplication;

/**
 * Created by shenhe on 2019/8/8.
 *
 * @description
 */
public class ApplicationUtil {

    private static String imei = "";

    /**
     * 获取手机IMEI号
     */
    public static String getIMEI() {
        if (!TextUtils.isEmpty(imei) && !"000000000000000".equals(imei)) {
            return imei;
        }
        try {
            TelephonyManager telephonyManager = (TelephonyManager) AeApplication.getAppContext()
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager.getDeviceId() == null || telephonyManager.getDeviceId().equals
                    ("")) {
                if (Build.VERSION.SDK_INT >= 23) {
                    imei = telephonyManager.getDeviceId(0);
                } else {
                    imei = "000000000000000";
                }
            } else {
                imei = telephonyManager.getDeviceId();
            }
        } catch (Exception e) {
            imei = "000000000000000";
        } finally {
            return imei;
        }
    }
}
