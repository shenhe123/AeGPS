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
    public static void checkCommonUpdate(Activity context) {
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
                .checkNewApp(new CommonUpdateCallback(context));
    }

    /**
     * 更新有WIFI静默下载，下载完成后 1、非强制更新时，下次启动客户端时弹更新窗提示 2、强制更新时，立即安装
     * 更新无WIFI（2、3、4G环境），直接弹窗，询问用户是否下载
     *
     * @param context 上下文应用
     * @param isBookStore 是否来自书架页
     * @return
     */
    public static boolean checkUpdate(Activity context, final boolean isBookStore) {
        return new UpdateAppManager
                .Builder()
                //当前Activity
                .setActivity(context)
                //更新地址
                //只有wifi下进行，静默下载(只对静默下载有效)
                .setOnlyWifi()
                //false：不弹吐司  true：弹吐司
                .setShowToast(false)
                .build()
                .checkHasUpdate(new ShelfUpdateCallback(context, isBookStore));
    }

    /**
     * 弹检测更新对话框
     * 更新有WIFI静默下载，下载完成后 1、非强制更新时，下次启动客户端时弹更新窗提示 2、强制更新时，立即安装
     * 更新无WIFI（2、3、4G环境），直接弹窗，询问用户是否下载
     *
     * @param context 上下文引用
     * @param isBookStore 是否来自书架页
     */
    public static void showProgressUpdateDialog(Activity context, final boolean isBookStore) {
        new UpdateAppManager
                .Builder()
                //当前Activity
                .setActivity(context)
                //更新地址
                //只有wifi下进行，静默下载(只对静默下载有效)
                .setOnlyWifi()
                .setShowToast(true)
                //false：通知栏显示进度条  true：不显示进度
                .dismissNotificationProgress(false)
                .build()
                .checkNewApp(new UserUpdateCallback(context, isBookStore) {

                    @Override
                    protected void onAfter() {
                        ((BaseActivity) context).stopProgressDialog();
                    }

                    @Override
                    protected void onBefore() {
                        ((BaseActivity) context).startProgressDialog(context.getResources().getString(R.string.check_app));
                    }
                });
    }

    /**
     * 更新有WIFI静默下载，非强制更新下载完成后下次启动客户端时弹更新窗提示，强制更新直接弹窗并下载
     * 更新无WIFI，不弹窗
     * 静默下载，并且自定义对话框
     *
     * @param context 上下文应用
     * @param isToast 是否弹吐司，告知用户下载状态
     */
    public static void silenceUpdateApp(Activity context, final boolean isToast, final Handler handler) {
        new UpdateAppManager
                .Builder()
                //当前Activity
                .setActivity(context)
                //更新地址
                //只有wifi下进行，静默下载(只对静默下载有效)
                .setOnlyWifi()
                .setShowToast(isToast)
                //false：通知栏显示进度条  true：不显示进度
                .dismissNotificationProgress(true)
                .build()
                .checkNewApp(new SilenceUpdateCallback(context, handler));
    }
}
