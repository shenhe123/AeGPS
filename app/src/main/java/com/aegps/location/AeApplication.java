package com.aegps.location;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;

import com.aegps.location.base.BaseApplication;
import com.aegps.location.greendb.base.MySQLiteOpenHelper;
import com.aegps.location.greendb.gen.DaoMaster;
import com.aegps.location.greendb.gen.DaoSession;
import com.aegps.location.locationservice.LocationChangBroadcastReceiver;
import com.aegps.location.utils.LogUtil;
import com.aegps.location.utils.SharedPrefUtils;
import com.baidu.location.LocationClient;
import com.bumptech.glide.Glide;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by ShenHe on 2019/3/3.
 */

public class AeApplication extends BaseApplication {

    private static LocationChangBroadcastReceiver locationChangBroadcastReceiver;

    /*
     * 数据库
     */
    private MySQLiteOpenHelper mySQLiteOpenHelper;
    private static DaoSession daoSession;
    private DaoMaster daoMaster;
    private String db_name = "yhdb.db";
    private SQLiteDatabase db;
    private int count = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化SharedPrefrence
        SharedPrefUtils.init(this);

        locationChangBroadcastReceiver = new LocationChangBroadcastReceiver();

        //setAgreePrivacy接口需要在LocationClient实例化之前调用
        //如果setAgreePrivacy接口参数设置为了false，则定位功能不会实现
        //true，表示用户同意隐私合规政策
        //false，表示用户不同意隐私合规政策
        LocationClient.setAgreePrivacy(true);

        loginDB();

        LogUtil.init(BuildConfig.DEBUG,"");
        CrashReport.initCrashReport(getApplicationContext(), "184a61a521", true);

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                count ++;
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                if(count > 0) {
                    count--;
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }

    /**
     * 判断app是否在后台
     * @return
     */
    public boolean isBackground(){
        if(count <= 0){
            return true;
        } else {
            return false;
        }
    }

    private void loginDB() {
        mySQLiteOpenHelper = new MySQLiteOpenHelper(getApplicationContext(),db_name,null);
        db = mySQLiteOpenHelper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public static LocationChangBroadcastReceiver getlocationChangeBoardcase() {
        if (null == locationChangBroadcastReceiver) {
            locationChangBroadcastReceiver = new LocationChangBroadcastReceiver();
        }

        return locationChangBroadcastReceiver;
    }

    public static DaoSession getDao() {
        return daoSession;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.with(this).onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Glide.with(this).onLowMemory();
        }
        Glide.with(this).onTrimMemory(level);
    }
}
