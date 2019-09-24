package com.aegps.location;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;

import com.aegps.location.base.BaseApplication;
import com.aegps.location.greendb.base.MySQLiteOpenHelper;
import com.aegps.location.greendb.gen.DaoMaster;
import com.aegps.location.greendb.gen.DaoSession;
import com.aegps.location.locationservice.LocationChangBroadcastReceiver;
import com.aegps.location.push.CustomPushReceiver;
import com.aegps.location.utils.LogUtil;
import com.aegps.location.utils.SharedPrefUtils;
import com.bumptech.glide.Glide;
import com.tencent.bugly.crashreport.CrashReport;
import com.xuexiang.xpush.XPush;
import com.xuexiang.xpush.core.IPushInitCallback;
import com.xuexiang.xpush.huawei.HuaweiPushClient;
import com.xuexiang.xpush.jpush.JPushClient;
import com.xuexiang.xpush.xiaomi.XiaoMiPushClient;
import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.system.RomUtils;

import static com.xuexiang.xutil.system.RomUtils.SYS_EMUI;
import static com.xuexiang.xutil.system.RomUtils.SYS_MIUI;

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
        XUtil.init(this);
        initPush();
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



    /**
     * 初始化推送
     */
    private void initPush() {
        XPush.debug(BuildConfig.DEBUG);
        //手动注册
//        XPush.init(this, new UMengPushClient());
        //自动注册
        XPush.init(this, new IPushInitCallback() {
            @Override
            public boolean onInitPush(int platformCode, String platformName) {
                String romName = RomUtils.getRom().getRomName();
                if (romName.equals(SYS_EMUI)) {
                    return platformCode == HuaweiPushClient.HUAWEI_PUSH_PLATFORM_CODE && platformName.equals(HuaweiPushClient.HUAWEI_PUSH_PLATFORM_NAME);
                } else if (romName.equals(SYS_MIUI)) {
                    return platformCode == XiaoMiPushClient.MIPUSH_PLATFORM_CODE && platformName.equals(XiaoMiPushClient.MIPUSH_PLATFORM_NAME);
                } else {
                    return platformCode == JPushClient.JPUSH_PLATFORM_CODE && platformName.equals(JPushClient.JPUSH_PLATFORM_NAME);
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Android8.0静态广播注册失败解决方案一：动态注册
            XPush.registerPushReceiver(new CustomPushReceiver());

            //Android8.0静态广播注册失败解决方案二：修改发射器
//            XPush.setIPushDispatcher(new Android26PushDispatcherImpl(CustomPushReceiver.class));
        }

        XPush.register();
    }

}
