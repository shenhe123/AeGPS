package com.aegps.location.utils.download;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import java.util.List;

/**
 * Created by vread on 2016/8/29.
 */
public class DownloadUtil {
    private DownloadHttpTool mDownloadHttpTool;
    private OnDownloadListener onDownloadListener;
    public boolean isStart = false;
    private int fileSize;
    private int downloadedSize = 0;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            int length = msg.arg1;
            if (length == -1) {
                mDownloadHttpTool.compelete();
                if (onDownloadListener != null) {
                    onDownloadListener.downloadEnd(true);
                }
                return;
            }
            if (length == -2){
                mDownloadHttpTool.compelete();
                if (onDownloadListener != null) {
                    onDownloadListener.downloadEnd(false);
                }
                return;
            }
            synchronized (this) {
                downloadedSize += length;
            }
            if (onDownloadListener != null) {
                onDownloadListener.downloadProgress(downloadedSize);
            }
        }

    };

    public DownloadUtil(int threadCount, String filePath, String filename,
                        String urlString, Context context) {
        mDownloadHttpTool = new DownloadHttpTool(threadCount, urlString,
                filePath, filename, context, mHandler);
    }

    @SuppressLint("StaticFieldLeak")
    public void start() {
        isStart = true;
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... arg0) {
                mDownloadHttpTool.ready();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                fileSize = mDownloadHttpTool.getFileSize();
                downloadedSize = mDownloadHttpTool.getCompeleteSize();
                if (onDownloadListener != null) {
                    onDownloadListener.downloadStart(fileSize);
                }
                mDownloadHttpTool.start();
            }
        }.execute();
    }

    public void pause() {
        isStart = false;
        mDownloadHttpTool.pause();
    }

    public void delete() {
        mDownloadHttpTool.delete();
    }

    public void reset() {
        mDownloadHttpTool.delete();
        start();
    }

    public void setOnDownloadListener(OnDownloadListener onDownloadListener) {
        this.onDownloadListener = onDownloadListener;
    }

    public interface OnDownloadListener {
        void downloadStart(int fileSize);

        void downloadProgress(int downloadedSize);

        void downloadEnd(boolean isSuccess);
    }

    /**
     * 判断某个界面是否在前台
     *
     * @param activity 要判断的Activity
     * @return 是否在前台显示
     */
    public static boolean isForeground(Activity activity) {
        return isForeground(activity, activity.getClass().getName());
    }

    /**
     * 判断某个界面是否在前台
     *
     * @param context   Context
     * @param className 界面的类名
     * @return 是否在前台显示
     */
    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className))
            return false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName()))
                return true;
        }
        return false;
    }

}
