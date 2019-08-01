package com.aegps.location.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.aegps.location.R;
import com.aegps.location.utils.Contants;
import com.aegps.location.utils.LocationUtil;
import com.aegps.location.utils.ThreadManager;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**前台Service，使用startForeground
 * 这个Service尽量要轻，不要占用过多的系统资源，否则
 * 系统在资源紧张时，照样会将其杀死
 *
 * Created by shenhe on 2019/7/30.
 */
public class DaemonService extends Service {
    private static final String TAG = "DaemonService";
    private static String namespace = "http://service.test.com/";
    private static String url = "http://10.0.2.2:8080/test_ws/services/TestService";
    private static String invokeMethod = "test";
    public static final int NOTICE_ID = 100;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(Contants.DEBUG)
            Log.d(TAG,"DaemonService---->onCreate被调用，启动前台service");
        //如果API大于18，需要弹出一个可见通知
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            Notification.Builder builder = new Notification.Builder(this);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle("KeepAppAlive");
            builder.setContentText("DaemonService is runing...");
            startForeground(NOTICE_ID,builder.build());
            // 如果觉得常驻通知栏体验不好
            // 可以通过启动CancelNoticeService，将通知移除，oom_adj值不变
            Intent intent = new Intent(this,CancelNoticeService.class);
            startService(intent);
        }else{
            startForeground(NOTICE_ID,new Notification());
        }

        uploadLocation();
    }

    private void uploadLocation() {
        String lngAndLat = LocationUtil.getLngAndLat(DaemonService.this);
        ThreadManager.getThreadPollProxy().execute(() -> {
            SoapObject so = new SoapObject(namespace, invokeMethod);
            //test方法无参数则无需设置，如果需传参数则按下面方式设置
//			so.addAttribute("name", "andy");
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = so;
            envelope.setOutputSoapObject(so);
            HttpTransportSE ht = new HttpTransportSE(url);
            ht.debug = true;
            Object obj = null;
            try {
                ht.call(namespace+invokeMethod, envelope);
                //test方法返回的是String类型，所以用Object来接收
                obj = envelope.getResponse();
                String response = obj.toString();
                //如果test方法返回UserInfo对象,则可以采用下面方式接收
                //假设UserInfo包含属性：String name,int age,char sex
//				so = (SoapObject) envelope.getResponse();
//				if(null != so){
//					String name = so.getPropertyAsString("name");
//					int age = (Integer) so.getProperty("age");
//					char sex = (Character) so.getProperty("sex");
//				}
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 如果Service被终止
        // 当资源允许情况下，重启service
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 如果Service被杀死，干掉通知
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            NotificationManager mManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            mManager.cancel(NOTICE_ID);
        }
        if(Contants.DEBUG)
            Log.d(TAG,"DaemonService---->onDestroy，前台service被杀死");
        // 重启自己
        Intent intent = new Intent(getApplicationContext(),DaemonService.class);
        startService(intent);
    }
}
