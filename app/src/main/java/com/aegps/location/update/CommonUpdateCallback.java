package com.aegps.location.update;


import android.content.Context;
import android.view.View;

import com.aegps.location.R;
import com.aegps.location.update.service.DownloadService;
import com.aegps.location.update.utils.AppUpdateUtils;
import com.aegps.location.utils.AppManager;
import com.aegps.location.utils.toast.ToastUtil;
import com.aegps.location.widget.dialog.UpdateAppDialog;

import java.io.File;

/**
 * Created by Vector
 * on 2017/7/20 0020.
 */

public class CommonUpdateCallback extends UpdateCallback {

    private Context mContext;

    public CommonUpdateCallback(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected final void hasNewApp(final UpdateAppBean updateAppBean, final UpdateAppManager updateAppManager) {
        /**
         * 更新有WIFI静默下载，下载完成后 1、非强制更新时，下次启动客户端时弹更新窗提示 2、强制更新时，立即安装
         * 更新无WIFI（2、3、4G环境），直接弹窗，询问用户是否下载
         */
        if (AppUpdateUtils.appIsDownloaded(updateAppBean)) {
            //如果是强制更新，弹窗并下载
            if(updateAppBean.isConstraint()) {
                showForceDialog(updateAppBean, AppUpdateUtils.getAppFile(updateAppBean));
            } else {
                showDialog(updateAppBean, AppUpdateUtils.getAppFile(updateAppBean));
            }
        } else {
            //假如是onlyWifi,则进行判断网络环境
            if (updateAppBean.isOnlyWifi() && !AppUpdateUtils.isWifi(updateAppManager.getContext())) {
                //当前是2、3、4G环境
                show4GDialog(updateAppBean, updateAppManager);
                return;
            }

            download(updateAppManager);

            //如果是强制更新，弹窗并下载
            if(updateAppBean.isConstraint()) {
                showForceDialog(updateAppBean, AppUpdateUtils.getAppFile(updateAppBean));
            } else {
                showDialog(updateAppBean, AppUpdateUtils.getAppFile(updateAppBean));
            }
        }
    }

    private void download(UpdateAppManager updateAppManager) {
        updateAppManager.download();
    }

    /**
     * 无WiFi（2G\3G\4G），非强制更新对话框，
     *
     * @param updateApp        新app信息
     * @param updateAppManager 网路接口
     */
    protected void show4GDialog(UpdateAppBean updateApp, UpdateAppManager updateAppManager) {
        UpdateAppDialog updateAppDialog = new UpdateAppDialog(mContext);
        updateAppDialog.setMessage(updateApp.getUpdateLog());
        updateAppDialog.setLeft(mContext.getString(R.string.update_app_new));
        updateAppDialog.setRight(mContext.getString(R.string.update_app_not));
        updateAppDialog.setLiftButtonListener(dialog -> {
            download(updateAppManager);
        });
        updateAppDialog.setRightButtonListener(dialog -> {});
        updateAppDialog.setCancelable(true);
        updateAppDialog.setProgressVisible(View.GONE);
        updateAppDialog.show();
    }

    /**
     * 有WiFi，非强制更新对话框，
     *
     * @param updateApp        新app信息
     * @param appFile          下载好的app文件
     */
    protected void showDialog(UpdateAppBean updateApp, File appFile) {
        UpdateAppDialog updateAppDialog = new UpdateAppDialog(mContext);
        updateAppDialog.setMessage(updateApp.getUpdateLog());
        updateAppDialog.setLeft(mContext.getString(R.string.update_app_new));
        updateAppDialog.setRight(mContext.getString(R.string.update_app_not));
        updateAppDialog.setLiftButtonListener(dialog -> {
            if (DownloadService.isRunning) {
                if (updateAppDialog.getProgressVisible() != View.VISIBLE) {
                    updateAppDialog.setProgressVisible(View.VISIBLE);
                } else {
                    ToastUtil.show("安装包正在下载中");
                }
            } else {
                AppUpdateUtils.installApp(mContext, appFile);
            }
        });
        updateAppDialog.setRightButtonListener(dialog -> {});
        updateAppDialog.setCancelable(true);
        updateAppDialog.setProgressVisible(View.GONE);
        updateAppDialog.show();
    }

    /**
     * 有WiFi，强制更新对话框，
     *
     * @param updateApp        新app信息
     * @param appFile          下载好的app文件
     */
    protected void showForceDialog(UpdateAppBean updateApp, File appFile) {
        UpdateAppDialog updateAppDialog = new UpdateAppDialog(mContext);
        updateAppDialog.setMessage(updateApp.getUpdateLog());
        updateAppDialog.setLeft(!AppUpdateUtils.appIsDownloaded(updateApp) ? mContext.getString(R.string.update_app_use_new) : mContext.getString(R.string.update_app_free_flow));
        updateAppDialog.setRight(mContext.getString(R.string.update_app_quit));
        updateAppDialog.setLiftButtonListener(dialog -> {
            if (DownloadService.isRunning) {
                if (updateAppDialog.getProgressVisible() != View.VISIBLE) {
                    updateAppDialog.setProgressVisible(View.VISIBLE);
                } else {
                    ToastUtil.show("安装包正在下载中");
                }
            } else {
                AppUpdateUtils.installApp(mContext, appFile);
            }
        });
        updateAppDialog.setRightButtonListener(dialog -> AppManager.getAppManager().finishAllActivity());
        updateAppDialog.setCancelable(false);
        updateAppDialog.show();
    }
}
