package com.aegps.location.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.aegps.location.R;
import com.aegps.location.api.network.Callback;
import com.aegps.location.api.tool.SoapUtil;
import com.aegps.location.utils.ApplicationUtil;
import com.aegps.location.utils.Contants;
import com.aegps.location.utils.LocationUtil;
import com.aegps.location.utils.LogUtil;
import com.aegps.location.utils.SharedPrefUtils;
import com.aegps.location.utils.ThreadManager;
import com.aegps.location.utils.toast.ToastUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 前台Service，使用startForeground
 * 这个Service尽量要轻，不要占用过多的系统资源，否则
 * 系统在资源紧张时，照样会将其杀死
 * <p>
 * Created by shenhe on 2019/7/30.
 */
public class DaemonService extends Service {
    private static final String TAG = "DaemonService";
    public static final int NOTICE_ID = 100;
    private static final String CHANNEL_ID_STRING = "channel_id_string";
    public static boolean isRunning = false;
    private Timer mRunTimer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Contants.DEBUG)
            Log.d(TAG, "DaemonService---->onCreate被调用，启动前台service");
        isRunning = true;
        //如果API大于18，需要弹出一个可见通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //适配8.0service
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel mChannel = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                mChannel = new NotificationChannel(CHANNEL_ID_STRING, "云物流", NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(mChannel);
                Notification notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID_STRING).build();
                startForeground(1, notification);
            } else {
                Notification.Builder builder = new Notification.Builder(this);
                builder.setSmallIcon(R.mipmap.ic_launcher);
                builder.setContentTitle("KeepAppAlive");
                builder.setContentText("DaemonService is runing...");
                startForeground(NOTICE_ID, builder.build());
            }
            // 如果觉得常驻通知栏体验不好
            // 可以通过启动CancelNoticeService，将通知移除，oom_adj值不变
            Intent intent = new Intent(this, CancelNoticeService.class);
            startService(intent);
        } else {
            startForeground(NOTICE_ID, new Notification());
        }
    }

    private void uploadLocation() {
        String lngAndLat = LocationUtil.getLngAndLat(DaemonService.this);
        String[] lngAndLatArray = lngAndLat.split(",");
        LogUtil.d(lngAndLat);
        ThreadManager.getThreadPollProxy().execute(() -> {
            SoapUtil.getInstance().locationTargeting(ApplicationUtil.getIMEI(),
                    SharedPrefUtils.getString(Contants.SP_DATABASE_NAME),
                    lngAndLatArray[0],
                    lngAndLatArray[1],
                    new Callback() {
                        @Override
                        public void onResponse(boolean success, String data) {
                            if (success) {
                                LogUtil.d(data);
                            } else {
                                SoapUtil.onFailure(data);
                            }
                        }

                        @Override
                        public void onFailure(Object o) {
                            ToastUtil.showShort(o.toString());
                        }
                    });
        });
    }

    private void startRunTimer() {
        TimerTask mTask = new TimerTask() {
            @Override
            public void run() {
                uploadLocation();
            }
        };
        mRunTimer = new Timer();
        // 每隔1s更新一下时间
        mRunTimer.schedule(mTask, 1000, 1000 * 60 * SharedPrefUtils.getInt(Contants.SP_UPLOAD_INTERVAL_DURATION, 2));
    }

    private void stopRunTimer() {
        if (mRunTimer != null) {
            mRunTimer.cancel();
            mRunTimer = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 如果Service被终止
        // 当资源允许情况下，重启service
        startRunTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 如果Service被杀死，干掉通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            NotificationManager mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mManager.cancel(NOTICE_ID);
        }
        if (Contants.DEBUG)
            Log.d(TAG, "DaemonService---->onDestroy，前台service被杀死");
        isRunning = false;
        stopRunTimer();
        // 重启自己
        Intent intent = new Intent(getApplicationContext(), DaemonService.class);
        startService(intent);
    }
}
