package com.aegps.location.update;

import android.app.Activity;
import android.os.Handler;

import com.aegps.location.R;
import com.aegps.location.base.BaseActivity;

/**
 * Created by shenhe on 2019/5/20.
 */
public class UpdateAppHelper {
    private static final String TAG = UpdateAppHelper.class.getSimpleName();

    /**
     * 更新有WIFI静默下载，下载完成后 1、非强制更新时，下次启动客户端时弹更新窗提示 2、强制更新时，立即安装
     * 更新无WIFI（2、3、4G环境），直接弹窗，询问用户是否下载
     *
     * @param context 上下文应用
     * @return
     */
    public static void checkCommonUpdate(Activity context, Handler handler) {
        new UpdateAppManager
                .Builder()
                //当前Activity
                .setActivity(context)
                //更新地址
                //只有wifi下进行，静默下载(只对静默下载有效)
                .setOnlyWifi()
                //false：不弹吐司  true：弹吐司
                .setShowToast(false)
                .build()
                .checkNewApp(new CommonUpdateCallback(context, handler));
    }
}
