package com.aegps.location.update;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.aegps.location.api.network.Callback;
import com.aegps.location.api.tool.SoapUtil;
import com.aegps.location.bean.net.CheckUpdateResult;
import com.aegps.location.update.listener.ExceptionHandler;
import com.aegps.location.update.listener.ExceptionHandlerHelper;
import com.aegps.location.update.service.DownloadService;
import com.aegps.location.utils.ThreadManager;
import com.aegps.location.utils.toast.ToastUtil;

import java.util.Map;

/**
 * 版本更新管理器
 */
public class UpdateAppManager {
    private static final String TAG = UpdateAppManager.class.getSimpleName();
    private Activity mActivity;
    public static UpdateAppBean mUpdateApp;
    private String mTargetPath;
    private boolean mHideDialog;
    private boolean mShowIgnoreVersion;
    private boolean mDismissNotificationProgress;//是否显示进度条  true：不显示
    private boolean mOnlyWifi;
    private boolean mShowToast;

    private UpdateAppManager(Builder builder) {
        mActivity = builder.getActivity();
        ;
        mTargetPath = builder.getTargetPath();
        mHideDialog = builder.isHideDialog();
        mShowIgnoreVersion = builder.isShowIgnoreVersion();
        mDismissNotificationProgress = builder.isDismissNotificationProgress();
        mOnlyWifi = builder.isOnlyWifi();
        mShowToast = builder.isShowToast();
    }

    public Context getContext() {
        return mActivity;
    }

    /**
     * @return 新版本信息
     */
    public UpdateAppBean fillUpdateAppData() {
        if (mUpdateApp != null) {
            mUpdateApp.setTargetPath(mTargetPath);
            mUpdateApp.setHideDialog(mHideDialog);
            mUpdateApp.showIgnoreVersion(mShowIgnoreVersion);
            mUpdateApp.dismissNotificationProgress(mDismissNotificationProgress);
            mUpdateApp.setOnlyWifi(mOnlyWifi);
            mUpdateApp.setShowToast(mShowToast);
            return mUpdateApp;
        }

        return null;
    }

    /**
     * 最简方式
     */
    public void update() {
        checkNewApp(new UpdateCallback());
    }

    /**
     * 检测是否有新版本
     *
     * @param callback 更新回调
     */
    public void checkNewApp(final UpdateCallback callback) {
        if (callback == null) {
            return;
        }
        callback.onBefore();
        ThreadManager.getThreadPollProxy().execute(() -> SoapUtil.getInstance().checkUpdate(new Callback() {
            @Override
            public void onResponse(boolean success, String data) {
                if (success) {
                    CheckUpdateResult checkUpdateResult = SoapUtil.getGson().fromJson(data, CheckUpdateResult.class);
                    if (checkUpdateResult == null || checkUpdateResult.getReturnTable() == null || checkUpdateResult.getReturnTable().size() == 0) {
                        if (mShowToast) {
                            callback.noNewApp("已是最新版本");
                        }
                        return;
                    }
                    CheckUpdateResult.ReturnTableBean returnTableBean = checkUpdateResult.getReturnTable().get(0);
                    if (!returnTableBean.isIsUpdate()) {
                        if (mShowToast) {
                            callback.noNewApp("已是最新版本");
                        }
                        return;
                    }
                    if (mUpdateApp == null) mUpdateApp = new UpdateAppBean();
                    mUpdateApp.setUpdate(returnTableBean.isIsUpdate() ? "Y" : "N");
                    mUpdateApp.setNewVersion("");
                    mUpdateApp.setApkFileUrl(returnTableBean.getDownloadLink());
                    mUpdateApp.setConstraint(returnTableBean.isIsForcedUpdate());
                    mUpdateApp.setNewMd5(returnTableBean.getMD5Value());
                    mUpdateApp.setUpdateLog(returnTableBean.getUpdateDescribe());
                    mUpdateApp.setUseMd5Check(returnTableBean.isIsMD5Check());
                    mUpdateApp.setApkName(returnTableBean.getApkName().endsWith(".apk") ? returnTableBean.getApkName() : returnTableBean.getApkName() + ".apk");
                    fillUpdateAppData();
                    if (mActivity == null || mActivity.isFinishing()) return;
                    mActivity.runOnUiThread(() -> callback.hasNewApp(mUpdateApp, UpdateAppManager.this));

                } else {
                    if (mShowToast) {
                        callback.noNewApp(data);
                    }
                }
            }

            @Override
            public void onFailure(Object o) {
                if (mShowToast) {
                    callback.noNewApp(o.toString());
                }
            }

            @Override
            public void onMustRun() {
                callback.onAfter();
            }
        }));
    }

    /**
     * 如果已经请求过检查更新，则直接弹窗
     *
     * @param callback
     * @return
     */
    public boolean checkHasUpdate(final UpdateCallback callback) {
        if (mUpdateApp != null) {
            if (!TextUtils.isEmpty(mUpdateApp.getUpdate()) && TextUtils.equals("Y", mUpdateApp.getUpdate())) {
                callback.hasNewApp(mUpdateApp, UpdateAppManager.this);
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 后台下载
     */
    public void download() {
        if (mUpdateApp == null) {
            throw new NullPointerException("updateApp 不能为空");
        }
        if ((DownloadService.isRunning)) {
            if (mShowToast) {
                ToastUtil.show("安装包正在下载中");
            }
            return;
        }
        DownloadService.startService(mActivity, mUpdateApp);
    }

    public static class Builder {
        //必须有
        private Activity mActivity;
        //必须有
        private HttpManager mHttpManager;
        //4,apk的下载路径
        private String mTargetPath;
        //6,自定义参数
        private Map<String, String> params;
        //7,是否隐藏对话框下载进度条
        private boolean mHideDialog = false;
        private boolean mShowIgnoreVersion;
        private boolean dismissNotificationProgress;
        private boolean mOnlyWifi;
        private boolean mShowToast;

        public Map<String, String> getParams() {
            return params;
        }

        /**
         * 自定义请求参数
         *
         * @param params 自定义请求参数
         * @return Builder
         */
        public Builder setParams(Map<String, String> params) {
            this.params = params;
            return this;
        }

        public String getTargetPath() {
            return mTargetPath;
        }

        /**
         * apk的下载路径，
         *
         * @param targetPath apk的下载路径，
         * @return Builder
         */
        public Builder setTargetPath(String targetPath) {
            mTargetPath = targetPath;
            return this;
        }

        public Activity getActivity() {
            return mActivity;
        }

        /**
         * 是否是post请求，默认是get
         *
         * @param activity 当前提示的Activity
         * @return Builder
         */
        public Builder setActivity(Activity activity) {
            mActivity = activity;
            return this;
        }

        /**
         * @return 生成app管理器
         */
        public UpdateAppManager build() {
            //校验
            if (getActivity() == null) {
                throw new NullPointerException("必要参数不能为空");
            }
            if (TextUtils.isEmpty(getTargetPath())) {
                //sd卡是否存在
                String path = "";
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable()) {
                    path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                } else {
                    path = getActivity().getCacheDir().getAbsolutePath();
                }
                setTargetPath(path);
            }
            return new UpdateAppManager(this);
        }

        /**
         * 是否隐藏对话框下载进度条
         *
         * @return Builder
         */
        public Builder hideDialogOnDownloading() {
            mHideDialog = true;
            return this;
        }

        /**
         * @return 是否影藏对话框
         */
        public boolean isHideDialog() {
            return mHideDialog;
        }

        /**
         * 显示忽略版本
         *
         * @return 是否忽略版本
         */
        public Builder showIgnoreVersion() {
            mShowIgnoreVersion = true;
            return this;
        }

        public boolean isShowIgnoreVersion() {
            return mShowIgnoreVersion;
        }

        /**
         * 不显示通知栏进度条
         *
         * @return 是否显示进度条
         */
        public Builder dismissNotificationProgress(boolean dismissNotificationProgress) {
            this.dismissNotificationProgress = dismissNotificationProgress;
            return this;
        }

        public boolean isDismissNotificationProgress() {
            return dismissNotificationProgress;
        }

        public Builder setOnlyWifi() {
            mOnlyWifi = true;
            return this;
        }

        public boolean isOnlyWifi() {
            return mOnlyWifi;
        }

        public boolean isShowToast() {
            return mShowToast;
        }

        public Builder setShowToast(boolean isToast) {
            this.mShowToast = isToast;
            return this;
        }

        public Builder handleException(ExceptionHandler exceptionHandler) {
            ExceptionHandlerHelper.init(exceptionHandler);
            return this;
        }

    }

}

