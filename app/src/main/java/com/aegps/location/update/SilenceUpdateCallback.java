package com.aegps.location.update;


import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.View;

import com.aegps.location.R;
import com.aegps.location.update.service.DownloadService;
import com.aegps.location.update.utils.AppUpdateUtils;
import com.aegps.location.utils.AppManager;
import com.aegps.location.utils.toast.ToastUtil;
import com.aegps.location.widget.dialog.UpdateAppDialog;

import java.io.File;

/**
 * Create by shenhe on 2019.07.23
 * 检查是否有更新
 * <p>
 * 有WiFi，有更新则静默下载
 * 无WiFi，有无更新都直接返回
 */
public class SilenceUpdateCallback extends UpdateCallback {

    private final Handler handler;
    private final Context mContext;

    public SilenceUpdateCallback(Context mContext, Handler handler) {
        this.mContext = mContext;
        this.handler = handler;
    }

    @Override
    protected final void hasNewApp(final UpdateAppBean updateAppBean, final UpdateAppManager updateAppManager) {
        if (AppUpdateUtils.appIsDownloaded(updateAppBean)) {
            //如果是强制更新，弹窗并下载
            if (updateAppBean.isConstraint()) {
                showForceDialog(updateAppBean, AppUpdateUtils.getAppFile(updateAppBean), updateAppManager);
            }
        } else {
            //假如是onlyWifi,则进行判断网络环境
            if (updateAppBean.isOnlyWifi() && !AppUpdateUtils.isWifi(updateAppManager.getContext())) {
                //当前是2、3、4G环境，不弹窗，直接返回
                return;
            }

            //有WiFi，有更新，静默下载
            updateAppManager.download();

            if (updateAppBean.isConstraint()) {//强制更新，直接弹窗
                showForceDialog(updateAppBean, AppUpdateUtils.getAppFile(updateAppBean), updateAppManager);
            }
        }
    }

    @Override
    protected void onAfter() {
        super.onAfter();
        //通知SplashActivity检查更新完成，开始初始化数据
        handler.sendEmptyMessage(0);
    }

    /**
     * 有WiFi，强制更新对话框，
     *
     * @param updateApp 新app信息
     * @param appFile   下载好的app文件
     */
    protected void showForceDialog(UpdateAppBean updateApp, File appFile, UpdateAppManager updateAppManager) {
        if (mContext instanceof Activity) {
            if (((Activity) mContext).isFinishing()) return;
        }
        UpdateAppDialog updateAppDialog = new UpdateAppDialog(mContext);
        updateAppDialog.setMessage(updateApp.getUpdateLog());
        updateAppDialog.setLeft(!AppUpdateUtils.appIsDownloaded(updateApp) ? mContext.getString(R.string.update_app_use_new) : mContext.getString(R
                .string.update_app_free_flow));
        updateAppDialog.setRight(mContext.getString(R.string.update_app_quit));
        updateAppDialog.setLiftButtonListener(dialog -> {
            if (DownloadService.isRunning) {
                if (updateAppDialog.getProgressVisible() != View.VISIBLE) {
                    updateAppDialog.setProgressVisible(View.VISIBLE);
                } else {
                    ToastUtil.show("安装包正在下载中");
                }
            } else {
                if (AppUpdateUtils.appIsDownloaded(updateApp)) {
                    AppUpdateUtils.installApp(mContext, appFile);
                } else {
                    ToastUtil.show("安装包正在下载中");
                    updateAppManager.download();
                }
            }
        });
        updateAppDialog.setRightButtonListener(dialog -> AppManager.getAppManager().finishAllActivity());
        updateAppDialog.setCancelable(false);
        updateAppDialog.show();
    }
}
