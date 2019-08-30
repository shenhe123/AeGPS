package com.aegps.location.update.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.aegps.location.AeApplication;
import com.aegps.location.R;
import com.aegps.location.bean.event.EBProgressEvent;
import com.aegps.location.update.HttpManager;
import com.aegps.location.update.UpdateAppBean;
import com.aegps.location.update.utils.AppUpdateUtils;
import com.aegps.location.utils.LogUtil;
import com.aegps.location.utils.download.DownloadUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;


/**
 * 后台下载
 */
public class DownloadService extends Service {

    private static final int NOTIFY_ID = 0;
    private static final String TAG = DownloadService.class.getSimpleName();
    private static final String CHANNEL_ID = "app_update_id";
    private static final CharSequence CHANNEL_NAME = "app_update_channel";
    private static final String EXTRA_UPDATE_DATA = "extra_update_data";
    private static final String EXTRA_UPDATE_CALLBACK = "extra_update_callback";

    public static boolean isRunning = false;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private int totalLength;
    public boolean mDismissNotificationProgress;
    private int progress;
    private UpdateAppBean updateAppBean;

    /**
     * 开启服务方法
     *
     * @param context
     */
    public static void startService(Context context, UpdateAppBean bean) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(EXTRA_UPDATE_DATA, bean);
        context.startService(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        isRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateAppBean = ((UpdateAppBean) intent.getSerializableExtra(EXTRA_UPDATE_DATA));
        startDownload(updateAppBean);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // 返回自定义的DownloadBinder实例
        return null;
    }

    @Override
    public void onDestroy() {
        mNotificationManager = null;
        super.onDestroy();
    }

    /**
     * 创建通知
     */
    private void setUpNotification() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            //设置绕过免打扰模式
//            channel.setBypassDnd(false);
//            //检测是否绕过免打扰模式
//            channel.canBypassDnd();
//            //设置在锁屏界面上显示这条通知
//            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
//            channel.setLightColor(Color.GREEN);
//            channel.setShowBadge(true);
//            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            channel.enableVibration(false);
            channel.enableLights(false);

            mNotificationManager.createNotificationChannel(channel);
        }


        mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        mBuilder.setContentTitle("安装包正在下载中...")
                .setContentText("正在连接服务器")
                .setSmallIcon(R.mipmap.ic_logo)
                .setLargeIcon(BitmapFactory
                        .decodeResource(AeApplication.getAppContext().getResources(),
                                R.mipmap.ic_launcher))
                .setOngoing(true)
                .setDefaults(Notification.DEFAULT_LIGHTS) //设置通知的提醒方式： 呼吸灯
                .setPriority(NotificationCompat.PRIORITY_MAX) //设置通知的优先级：最大
                .setAutoCancel(false)//设置通知被点击一次是否自动取消
                .setContentText("下载进度:" + "0%")
                .setProgress(100, 0, false)
                .setWhen(System.currentTimeMillis());
        if (!mDismissNotificationProgress) {
            mNotificationManager.notify(NOTIFY_ID, mBuilder.build());
        }
    }

    /**
     * 下载模块
     */
    private void startDownload(UpdateAppBean updateApp) {

        mDismissNotificationProgress = updateApp.isDismissNotificationProgress();

        String apkUrl = updateApp.getApkFileUrl();
//        apkUrl = "https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/apk/sample-debug.apk";
        if (TextUtils.isEmpty(apkUrl)) {
            String contentText = "新版本下载路径错误";
            stop(contentText);
            return;
        }
        String appName = AppUpdateUtils.getApkName(updateApp);

        File appDir = new File(updateApp.getTargetPath());
        if (!appDir.exists()) {
            appDir.mkdirs();
        }

        String target = appDir.getAbsolutePath();
        DownloadUtil downloadUtil = new DownloadUtil(1, target, appName, apkUrl, AeApplication.getAppContext());
        FileDownloadCallBack finalFileCallBack = new FileDownloadCallBack();
        downloadUtil.setOnDownloadListener(new DownloadUtil.OnDownloadListener() {

            @Override
            public void downloadStart(int fileSize) {
                totalLength = fileSize;
                progress = 0;
                finalFileCallBack.onBefore();
            }

            @Override
            public void downloadProgress(int downloadedSize) {
                if (progress != downloadedSize * 100 / totalLength) {
                    progress = downloadedSize * 100 / totalLength;
                }
                finalFileCallBack.onProgress(progress, totalLength);
            }

            @Override
            public void downloadEnd(boolean isSuccess) {
                if (isSuccess){
                    File file = new File(target + "/" + appName);
                    finalFileCallBack.onResponse(file);
                }else {
                    finalFileCallBack.onError("网络异常");
                }
            }
        });
        downloadUtil.start();
    }

    private void stop(String contentText) {
        if (mBuilder != null) {
            mBuilder.setContentTitle(AppUpdateUtils.getAppName(DownloadService.this))
                    .setContentText(contentText);
            Notification notification = mBuilder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            mNotificationManager.notify(NOTIFY_ID, notification);
        }
        close();
    }

    private void close() {
        stopSelf();
        isRunning = false;
    }

    class FileDownloadCallBack implements HttpManager.FileCallback {
        int oldRate = 0;

        @Override
        public void onBefore() {
            //初始化通知栏
            setUpNotification();
        }

        @Override
        public void onProgress(float progress, long total) {
            //做一下判断，防止自回调过于频繁，造成更新通知栏进度过于频繁，而出现卡顿的问题。
            int rate = Math.round(progress);
            if (oldRate != rate) {
                if (mBuilder != null && !mDismissNotificationProgress) {
                    mBuilder.setContentText("下载进度：" + rate + "%")
                            .setProgress(100, rate, false)
                            .setWhen(System.currentTimeMillis());
                    Notification notification = mBuilder.build();
                    notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE;
                    mNotificationManager.notify(NOTIFY_ID, notification);
                }
                LogUtil.d("progress = " + rate);
                EventBus.getDefault().post(new EBProgressEvent(rate, updateAppBean.isConstraint()));
                //重新赋值
                oldRate = rate;
            }
        }

        @Override
        public void onError(String error) {
//            Toast.makeText(DownloadService.this, "更新新版本出错，" + error, Toast.LENGTH_SHORT).show();
            //App前台运行
            try {
                Toast.makeText(DownloadService.this, "更新新版本出错，" + error, Toast.LENGTH_SHORT).show();
                mNotificationManager.cancel(NOTIFY_ID);
                close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        @Override
        public void onResponse(File file) {
            try {
                if (AppUpdateUtils.isAppOnForeground(DownloadService.this) || mBuilder == null) {
                    //App前台运行
                    mNotificationManager.cancel(NOTIFY_ID);
                } else {
                    //App后台运行
                    //更新参数,注意flags要使用FLAG_UPDATE_CURRENT
                    Intent installAppIntent = AppUpdateUtils.getInstallAppIntent(DownloadService.this, file);
                    PendingIntent contentIntent = PendingIntent.getActivity(DownloadService.this, 0, installAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(contentIntent)
                            .setContentTitle(AppUpdateUtils.getAppName(DownloadService.this))
                            .setContentText("下载完成，请点击安装")
                            .setProgress(0, 0, false)
                            //                        .setAutoCancel(true)
                            .setDefaults((Notification.DEFAULT_ALL));
                    Notification notification = mBuilder.build();
                    notification.flags = Notification.FLAG_AUTO_CANCEL;
                    mNotificationManager.notify(NOTIFY_ID, notification);
                }
                //下载完自杀
                close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                close();
            }
        }
    }
}
