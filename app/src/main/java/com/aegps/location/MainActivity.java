package com.aegps.location;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aegps.location.adapter.RefreshMonitorAdapter;
import com.aegps.location.api.network.Callback;
import com.aegps.location.api.tool.SoapUtil;
import com.aegps.location.base.BaseActivity;
import com.aegps.location.bean.event.CommonEvent;
import com.aegps.location.bean.net.RefreshMonitor;
import com.aegps.location.locationservice.CoordinateTransformUtil;
import com.aegps.location.locationservice.LocationChangBroadcastReceiver;
import com.aegps.location.locationservice.LocationService;
import com.aegps.location.locationservice.LocationStatusManager;
import com.aegps.location.locationservice.NotificationService;
import com.aegps.location.locationservice.Utils;
import com.aegps.location.utils.AppManager;
import com.aegps.location.utils.ApplicationUtil;
import com.aegps.location.utils.Contants;
import com.aegps.location.utils.LogUtil;
import com.aegps.location.utils.SharedPrefUtils;
import com.aegps.location.utils.ThreadManager;
import com.aegps.location.utils.toast.ToastUtil;
import com.aegps.location.widget.CustomView;
import com.aegps.location.widget.dialog.ExitAppDialog;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.amap.api.location.AMapLocation.LOCATION_SUCCESS;

/**
 * 运动界面，处理各种保活逻辑
 * <p>
 * Created by shenhe on 2019/7/30.
 */

public class MainActivity extends BaseActivity implements View.OnClickListener, AMapLocationListener {
    private static final String TAG = "MainActivity";
    public static final String MESSAGE_RECEIVED_ACTION = "com.aegps.location.ui.MESSAGE_RECEIVED_ACTION";
    public static final int SPAN = 1000 * 60 * SharedPrefUtils.getInt(Contants.SP_UPLOAD_INTERVAL_DURATION, 2);
    private static final long TIME_INTERVAL = 60 * 1000;
    /**
     * 装载启动
     */
    private LinearLayout mLayoutLoadingBegin;
    /**
     * 卸货签收
     */
    private LinearLayout mLayoutUnloadReceipt;
    /**
     * 运输变更
     */
    private LinearLayout mLayoutTransportChange;
    private CustomView mTransportId;
    private CustomView mCarNum;
    private CustomView mFreightRate;
    private CustomView mBeginTime;
    private CustomView mDrivingTime;
    private CustomView mDrivingDistance;
    private ImageView mIvLoadingBegin;
    private TextView mTvLoadingBegin;
    private ImageView mIvUnloadReceipt;
    private TextView mTvUnloadReceipt;
    private ImageView mIvTransportChange;
    private TextView mTvTransportChange;
    private ImageView mIvRefresh;
    private Timer mRunTimer;
    private Animation operatingAnim;
    public static boolean isForeground = false;
    private AMapLocationClient mlocationClient;
    private boolean locationSuccess = false;//定位是否成功
    private LatLng locationLatLng;//定位成功的经纬度
    private RecyclerView mRecyclerView;
    private RefreshMonitorAdapter mAdapter;
    private static final String NOTIFICATION_CHANNEL_NAME = "BackgroundLocation";
    private NotificationManager notificationManager = null;
    boolean isCreateChannel = false;
    private Intent alarmIntent = null;
    private PendingIntent alarmPi = null;
    private AlarmManager alarm = null;
    private boolean isFirst = false;

    private BroadcastReceiver alarmReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            // 重复定时任务
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarm.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + TIME_INTERVAL, alarmPi);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarm.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + TIME_INTERVAL, alarmPi);
            }
            if(intent.getAction().equals("LOCATION")){
                if(null != mlocationClient){
                    mlocationClient.startLocation();
                }
            }
        }
    };
    private List<RefreshMonitor.MonitorEntryTableBean> monitorEntryTable = new ArrayList<>();


    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
        initLocation();
        broadInit();
        refreshMonitor();
    }

    private void refreshMonitor() {
        ThreadManager.getThreadPollProxy().execute(() -> SoapUtil.getInstance().refreshMonitor(ApplicationUtil.getIMEI(), SharedPrefUtils.getString(Contants.SP_DATABASE_NAME), new Callback() {
            @Override
            public void onResponse(boolean success, String data) {
                if (success) {
                    RefreshMonitor refreshMonitor = SoapUtil.getGson().fromJson(data, RefreshMonitor.class);
                    if (refreshMonitor == null) {
                        runOnUiThread(() -> {
                            resetLoadingBeginEnable();
                            resetView();
                        });
                        return;
                    }
                    List<RefreshMonitor.MonitorHeaderTableBean> monitorHeaderTable = refreshMonitor.getMonitorHeaderTable();
                    if (monitorHeaderTable != null && monitorHeaderTable.size() > 0) {
                        RefreshMonitor.MonitorHeaderTableBean monitorHeaderTableBean = monitorHeaderTable.get(0);
                        runOnUiThread(() -> {
                            refreshHeaderView(monitorHeaderTableBean);
                            if (monitorHeaderTableBean.getTrafficMainID() != 0) {
                                if (!isFirst) {
                                    isFirst = true;
                                    startLocationService();
                                    startRunTimer();
                                }
                            }
                        });

                    }
                    monitorEntryTable = refreshMonitor.getMonitorEntryTable();
                    if (monitorEntryTable != null && monitorEntryTable.size() > 0) {
                        runOnUiThread(() -> mAdapter.setData(monitorEntryTable));
                    }
                    if ((monitorHeaderTable == null || monitorHeaderTable.size() <= 0) && (monitorEntryTable == null || monitorEntryTable.size() <= 0)) {
                        runOnUiThread(() -> {
                            resetLoadingBeginEnable();
                            resetView();
                        });
                    }
                } else {
                    SoapUtil.onFailure(data);
                }
            }

            @Override
            public void onFailure(Object o) {
                ToastUtil.show(o.toString());
            }

            @Override
            public void onMustRun() {
                //延迟1秒后动画停止
                mIvRefresh.postDelayed(() -> stopAnimRotate(), 1000);

            }
        }));
    }

    @Override
    public void initView() {
        EventBus.getDefault().register(this);

        initAlarm();

        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);

        mLayoutLoadingBegin = (LinearLayout) findViewById(R.id.layout_loading_begin);
        mLayoutLoadingBegin.setOnClickListener(this);
        mLayoutUnloadReceipt = (LinearLayout) findViewById(R.id.layout_unload_receipt);
        mLayoutUnloadReceipt.setOnClickListener(this);
        mLayoutTransportChange = (LinearLayout) findViewById(R.id.layout_transport_change);
        mLayoutTransportChange.setOnClickListener(this);
        mIvRefresh = ((ImageView) findViewById(R.id.iv_refresh));
        mIvRefresh.setOnClickListener(this);
        mIvLoadingBegin = ((ImageView) findViewById(R.id.iv_loading_begin));
        mTvLoadingBegin = ((TextView) findViewById(R.id.tv_loading_begin));
        mIvUnloadReceipt = ((ImageView) findViewById(R.id.iv_unload_receipt));
        mTvUnloadReceipt = ((TextView) findViewById(R.id.tv_unload_receipt));
        mIvTransportChange = ((ImageView) findViewById(R.id.iv_transport_change));
        mTvTransportChange = ((TextView) findViewById(R.id.tv_transport_change));

        mTransportId = (CustomView) findViewById(R.id.transport_id);
        mCarNum = (CustomView) findViewById(R.id.car_num);
        mFreightRate = (CustomView) findViewById(R.id.freight_rate);
        mBeginTime = (CustomView) findViewById(R.id.begin_time);
        mDrivingTime = (CustomView) findViewById(R.id.driving_time);
        mDrivingDistance = (CustomView) findViewById(R.id.driving_distance);

        mRecyclerView = ((RecyclerView) findViewById(R.id.recyclerView));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mRecyclerView.setNestedScrollingEnabled(false);
        mAdapter = new RefreshMonitorAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initAlarm() {
        // 创建Intent对象，action为LOCATION
        alarmIntent = new Intent();
        alarmIntent.setAction("LOCATION");
        IntentFilter ift = new IntentFilter();

        // 定义一个PendingIntent对象，PendingIntent.getBroadcast包含了sendBroadcast的动作。
        // 也就是发送了action 为"LOCATION"的intent
        alarmPi = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        // AlarmManager对象,注意这里并不是new一个对象，Alarmmanager为系统级服务
        alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

        //动态注册一个广播
        IntentFilter filter = new IntentFilter();
        filter.addAction("LOCATION");
        registerReceiver(alarmReceiver, filter);
    }

    private void initLocation() {
        mlocationClient = new AMapLocationClient(getApplicationContext());
        mlocationClient.setLocationListener(this);
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(SPAN);
        // 使用连续
        mLocationOption.setOnceLocation(false);
        mLocationOption.setLocationCacheEnable(false);
        // 地址信息
        mLocationOption.setNeedAddress(true);
        mlocationClient.setLocationOption(mLocationOption);
        mlocationClient.startLocation();
    }

    private void broadInit() {
        LocationChangBroadcastReceiver loc = AeApplication.getlocationChangeBoardcase();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LocationChangBroadcastReceiver.RECEIVER_ACTION);
        registerReceiver(loc, intentFilter);
    }

    private void resetView() {
        refreshHeaderView(new RefreshMonitor.MonitorHeaderTableBean());
        monitorEntryTable.clear();
        monitorEntryTable.add(new RefreshMonitor.MonitorEntryTableBean());
    }

    /**
     * 关闭服务
     * 先关闭守护进程，再关闭定位服务
     */
    private void stopLocationService() {
//        sendBroadcast(Utils.getCloseBrodecastIntent());
    }

    private void refreshHeaderView(RefreshMonitor.MonitorHeaderTableBean item) {
        if (item.getTrafficMainID() == 0) {
            resetLoadingBeginEnable();
        } else {
            resetUnloadReceiptEnable();
        }
        mTransportId.setRightText(item.getTrafficCode() == null ? "" : item.getTrafficCode());
        mCarNum.setRightText(item.getVehicleCode() == null ? "" : item.getVehicleCode());
        mFreightRate.setRightText(item.getShippingModeName() == null ? "" : item.getShippingModeName());
        mBeginTime.setRightText(item.getBeginningTime() == null ? "" : item.getBeginningTime());
        mDrivingTime.setRightText(item.getDrivingDuration() == null ? "" : item.getDrivingDuration());
        mDrivingDistance.setRightText(item.getMileageMeasure() + "公里");
    }

    private void startAnimRotate(){
        if (operatingAnim != null) {
            mIvRefresh.startAnimation(operatingAnim);
        }  else {
            mIvRefresh.setAnimation(operatingAnim);
            mIvRefresh.startAnimation(operatingAnim);
        }
    }

    private void stopAnimRotate(){
        mIvRefresh.clearAnimation();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CommonEvent event) {
        int code = event.getCode();
        switch (code) {
            default:
                break;
            case EasyCaptureActivity.EXTRA_LOAD_BEGIN_CODE:
                ToastUtil.show("启动成功");
                startLocationService();
                LocationStatusManager.getInstance().resetToInit(getApplicationContext());
                startRunTimer();
                break;
            case EasyCaptureActivity.EXTRA_UNLOAD_RECEIPT_CODE:
                ToastUtil.show("卸货成功");
                stopLocationService();
                LocationStatusManager.getInstance().resetToInit(getApplicationContext());
                stopRunTimer();
                break;
            case EasyCaptureActivity.EXTRA_TRANSPORT_CHANGE_CODE:
                ToastUtil.show("变更运输成功");
                break;
        }
        refreshMonitor();
    }

    private void startLocationService() {
        if (Utils.getInternet()) {//判断当然是否有网络
//            Intent intent = new Intent(MainActivity.this, LocationService.class);
//            if (Build.VERSION.SDK_INT >= 26) {
//                startForegroundService(intent);
//            } else {
//                startService(intent);
//            }

            if(null != alarm){
                //设置一个闹钟，2秒之后每隔一段时间执行启动一次定位程序
                // pendingIntent 为发送广播
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarm.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 2 * 1000, alarmPi);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarm.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 2 * 1000, alarmPi);
                } else {
                    alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 2 * 1000, TIME_INTERVAL, alarmPi);
                }

            }
        } else {
            ToastUtil.show("定位环境不佳，请检查网络或到空旷户外重新定位");
        }
    }

    private void resetLoadingBeginEnable() {
        //卸货签收不可用
        mLayoutUnloadReceipt.setClickable(false);
//        mIvUnloadReceipt.setImageResource(R.drawable.ic_unload_receipt);
        mTvUnloadReceipt.setTextColor(getResources().getColor(R.color.color_bbbbbb));
        //载货启动可用
        mLayoutLoadingBegin.setClickable(true);
//        mIvLoadingBegin.setImageResource(R.drawable.ic_load_start);
        mTvLoadingBegin.setTextColor(getResources().getColor(R.color.color_ff7c41));
        //运输变更可用
        mLayoutTransportChange.setClickable(true);
//        mIvTransportChange.setImageResource(R.drawable.ic_transport_change);
        mTvTransportChange.setTextColor(getResources().getColor(R.color.color_ff7c41));
        //刷新可用
        mIvRefresh.setClickable(false);
        mIvRefresh.setImageResource(R.drawable.ic_refresh_disable);


    }

    private void resetUnloadReceiptEnable() {
        //卸货签收可用
        mLayoutUnloadReceipt.setClickable(true);
//        mIvUnloadReceipt.setImageResource(R.drawable.ic_unload_receipt);
        mTvUnloadReceipt.setTextColor(getResources().getColor(R.color.color_ff7c41));
        //载货启动不可用
        mLayoutLoadingBegin.setClickable(false);
//        mIvLoadingBegin.setImageResource(R.drawable.ic_load_start);
        mTvLoadingBegin.setTextColor(getResources().getColor(R.color.color_bbbbbb));
        //运输变更可用
        mLayoutTransportChange.setClickable(false);
//        mIvTransportChange.setImageResource(R.drawable.ic_transport_change);
        mTvTransportChange.setTextColor(getResources().getColor(R.color.color_bbbbbb));
        //刷新不可用
        mIvRefresh.setClickable(true);
        mIvRefresh.setImageResource(R.drawable.ic_refresh_enable);
    }

    private void startRunTimer() {
        TimerTask mTask = new TimerTask() {
            @Override
            public void run() {
                uploadLoacationInfo();
                refreshMonitor();
            }
        };
        mRunTimer = new Timer();
        // 每隔1s更新一下时间
        mRunTimer.schedule(mTask, 1000, SPAN);
    }

    private void stopRunTimer() {
        if (mRunTimer != null) {
            mRunTimer.cancel();
            mRunTimer = null;
        }

        if(null != alarm){
            alarm.cancel(alarmPi);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 禁用返回键
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            ExitAppDialog exitAppDialog = new ExitAppDialog(MainActivity.this);
            exitAppDialog.setLiftButtonListener(dialog -> {
                stopLocationService();
                LocationStatusManager.getInstance().resetToInit(getApplicationContext());
                stopRunTimer();
                AppManager.getAppManager().finishAllActivity();
            });
            exitAppDialog.setRightButtonListener(dialog -> {
            });
            exitAppDialog.show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
        //切入前台后关闭后台定位功能
        if(null != mlocationClient) {
            mlocationClient.disableBackgroundLocation(true);
        }
    }

    @SuppressLint("NewApi")
    public Notification buildNotification() {

        Notification.Builder builder = null;
        Notification notification = null;
        if(android.os.Build.VERSION.SDK_INT >= 26) {
            //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
            if (null == notificationManager) {
                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }
            String channelId = getPackageName();
            if(!isCreateChannel) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId,
                        NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.enableLights(true);//是否在桌面icon右上角展示小圆点
                notificationChannel.setLightColor(Color.BLUE); //小圆点颜色
                notificationChannel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
                notificationChannel.setSound(null, null);
                notificationManager.createNotificationChannel(notificationChannel);
                isCreateChannel = true;
            }
            builder = new Notification.Builder(getApplicationContext(), channelId);
        } else {
            builder = new Notification.Builder(getApplicationContext());
            builder.setSound(null);
        }
        builder.setSmallIcon(R.mipmap.ic_logo)
                .setContentTitle("云物流")
                .setContentText("正在后台运行")
                .setWhen(System.currentTimeMillis());

        if (android.os.Build.VERSION.SDK_INT >= 16) {
            notification = builder.build();
        } else {
            return builder.getNotification();
        }
        return notification;
    }

    @Override
    protected void onStop() {
        super.onStop();
        boolean isBackground = ((AeApplication)getApplication()).isBackground();
        //如果app已经切入到后台，启动后台定位功能
        if(isBackground){
            if(null != mlocationClient) {
                //启动后台定位，第一个参数为通知栏ID，建议整个APP使用一个
                mlocationClient.enableBackgroundLocation(NotificationService.NOTI_ID, buildNotification());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }

        LocationChangBroadcastReceiver loc = AeApplication.getlocationChangeBoardcase();
        if (loc != null) {
            unregisterReceiver(loc);
        }

        if(null != alarmReceiver){
            unregisterReceiver(alarmReceiver);
            alarmReceiver = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.layout_loading_begin:
                EasyCaptureActivity.launch(this, getString(R.string.main_start_load), EasyCaptureActivity.EXTRA_LOAD_BEGIN_CODE);
                break;
            case R.id.layout_unload_receipt:
                EasyCaptureActivity.launch(this, getString(R.string.main_unload_receipt), EasyCaptureActivity.EXTRA_UNLOAD_RECEIPT_CODE);
                break;
            case R.id.layout_transport_change:
                EasyCaptureActivity.launch(this, getString(R.string.main_transport_change), EasyCaptureActivity.EXTRA_TRANSPORT_CHANGE_CODE);
                break;
            case R.id.iv_refresh:
                startAnimRotate();
                refreshMonitor();
                break;
        }
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        if (location != null && location.getErrorCode() == LOCATION_SUCCESS) {
            locationSuccess = true;
            double[] gcj02tobd09 = CoordinateTransformUtil.gcj02tobd09(location.getLongitude(), location.getLatitude());
            locationLatLng = new LatLng(gcj02tobd09[1], gcj02tobd09[0]);
            Log.e("shenhe 定位結果", "onLocationChanged: " + locationLatLng);
            SharedPrefUtils.saveString("locationLatLng", locationLatLng.latitude + "," + locationLatLng.longitude);
//            FilteWriterUtil.wirteToLoacal(FilteWriterUtil.getRootDir(AeApplication.getAppContext()) + "/1/log.txt"
//                    , "当前时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
//                            + "\nlongitude=" + locationLatLng.longitude
//                            + "\nlatitude=" + locationLatLng.latitude
//                            + "\ncountry=" + location.getCountry()
//                            + "\ncity=" + location.getCity()
//                            + "\nstreet=" + location.getStreet()
//                            + "\naddress=" + location.getAddress() + "\n\n");
        } else {
            locationSuccess = false;
        }
    }

    private void uploadLoacationInfo() {
        ThreadManager.getThreadPollProxy().execute(() -> {
            String locationLatLng = SharedPrefUtils.getString("locationLatLng");
            if (TextUtils.isEmpty(locationLatLng)) return;
            String[] split = locationLatLng.split(",");
            SoapUtil.getInstance().locationTargeting(ApplicationUtil.getIMEI(),
                    SharedPrefUtils.getString("sp_database_name"),
                    split[1] + "",
                    split[0] + "",
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
                            ToastUtil.show(o.toString());
                        }

                        @Override
                        public void onMustRun() {

                        }
                    });
        });
    }
}
