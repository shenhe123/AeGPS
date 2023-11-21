package com.aegps.location;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.SystemClock;
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

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aegps.location.adapter.RefreshMonitorAdapter;
import com.aegps.location.api.network.Callback;
import com.aegps.location.api.tool.SoapUtil;
import com.aegps.location.base.BaseActivity;
import com.aegps.location.bean.event.CommonEvent;
import com.aegps.location.bean.net.RefreshMonitor;
import com.aegps.location.locationservice.LocationChangBroadcastReceiver;
import com.aegps.location.locationservice.LocationService;
import com.aegps.location.locationservice.LocationStatusManager;
import com.aegps.location.locationservice.NotificationUtils;
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
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 运动界面，处理各种保活逻辑
 * <p>
 * Created by shenhe on 2019/7/30.
 */

public class MainActivity extends BaseActivity implements View.OnClickListener {
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
    private LocationClient mlocationClient;
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
//            // 重复定时任务
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                alarm.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + TIME_INTERVAL, alarmPi);
//            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                alarm.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + TIME_INTERVAL, alarmPi);
//            }
            if(intent.getAction().equals("LOCATION")){
                if(null != mlocationClient){
                    mlocationClient.start();
                }
            }
        }
    };
    private List<RefreshMonitor.MonitorEntryTableBean> monitorEntryTable = new ArrayList<>();
    private MyLocationListener myLocationListener;
    private Notification mNotification;


    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
        initLocationOption();
        mNotification = buildNotification();
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
        alarmPi = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE);
        // AlarmManager对象,注意这里并不是new一个对象，Alarmmanager为系统级服务
        alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

        //动态注册一个广播
        IntentFilter filter = new IntentFilter();
        filter.addAction("LOCATION");
        registerReceiver(alarmReceiver, filter);
    }

    /**
     * 初始化定位参数配置
     */

    private void initLocationOption() {
        if (mlocationClient == null) {
            try {
                //定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
                mlocationClient = new LocationClient(getApplicationContext());
                //声明LocationClient类实例并配置定位参数
                LocationClientOption locationOption = new LocationClientOption();
                myLocationListener = new MyLocationListener();
                //注册监听函数
                mlocationClient.registerLocationListener(myLocationListener);
                //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
                locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
                //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
                locationOption.setCoorType("gcj02");
                //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
                locationOption.setScanSpan(5000);
                //可选，设置是否需要地址信息，默认不需要
                locationOption.setIsNeedAddress(true);
                //可选，设置是否需要地址描述
                locationOption.setIsNeedLocationDescribe(true);
                //可选，设置是否需要设备方向结果
                locationOption.setNeedDeviceDirect(false);
                //可选，默认false，设置是否当Gnss有效时按照1S1次频率输出Gnss结果
                locationOption.setLocationNotify(true);
                //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
                locationOption.setIgnoreKillProcess(true);
                //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
                locationOption.setIsNeedLocationDescribe(true);
                //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
                locationOption.setIsNeedLocationPoiList(true);
                //可选，默认false，设置是否收集CRASH信息，默认收集
                locationOption.SetIgnoreCacheException(false);
                //可选，默认false，设置是否开启卫星定位
                locationOption.setOpenGnss(true);
                //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
                locationOption.setIsNeedAltitude(false);
                //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
                locationOption.setOpenAutoNotifyMode();
                //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
                locationOption.setOpenAutoNotifyMode(3000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
                //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
                mlocationClient.setLocOption(locationOption);
                //开始定位
                mlocationClient.start();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            //开始定位
            mlocationClient.start();
        }
    }

    /**
     * 实现定位回调
     */
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            //获取纬度信息
            double latitude = location.getLatitude();
            //获取经度信息
            double longitude = location.getLongitude();
            //获取定位精度，默认值为0.0f
            float radius = location.getRadius();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
            String coorType = location.getCoorType();
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            int errorCode = location.getLocType();

            Log.e("shenhe 定位結果", "onLocationChanged: " + latitude + "," + longitude);
            SharedPrefUtils.saveString("locationLatLng", latitude + "," + longitude);
        }
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
        sendBroadcast(Utils.getCloseBrodecastIntent());
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
            Intent intent = new Intent(MainActivity.this, LocationService.class);
            if (Build.VERSION.SDK_INT >= 26) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }

            if(null != alarm){
                //设置一个闹钟，2秒之后每隔一段时间执行启动一次定位程序
                // pendingIntent 为发送广播
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    alarm.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 2 * 1000, alarmPi);
//                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    alarm.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 2 * 1000, alarmPi);
//                } else {
                    alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 2 * 1000, TIME_INTERVAL, alarmPi);
//                }

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
        //如果app已经切入到后台，启动后台定位功能
        if(null != mlocationClient) {
            //启动后台定位，第一个参数为通知栏ID，建议整个APP使用一个
            //开启后台定位
            // 将定位SDK的SERVICE设置成为前台服务, 提高定位进程存活率
            if (mNotification != null) {
                mlocationClient.enableLocInForeground(1, mNotification);
                mlocationClient.start();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
        //关闭后台定位（true：通知栏消失；false：通知栏可手动划除）
        if(null != mlocationClient) {
            mlocationClient.disableLocInForeground(true);
            mlocationClient.start();
        }
    }

    public Notification buildNotification() {

//        Notification.Builder builder = null;
//        Notification notification = null;
//        if(android.os.Build.VERSION.SDK_INT >= 26) {
//            //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
//            if (null == notificationManager) {
//                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            }
//            String channelId = getPackageName();
//            if(!isCreateChannel) {
//                NotificationChannel notificationChannel = new NotificationChannel(channelId,
//                        NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
//                notificationChannel.enableLights(true);//是否在桌面icon右上角展示小圆点
//                notificationChannel.setLightColor(Color.BLUE); //小圆点颜色
//                notificationChannel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
//                notificationChannel.setSound(null, null);
//                notificationManager.createNotificationChannel(notificationChannel);
//                isCreateChannel = true;
//            }
//            builder = new Notification.Builder(getApplicationContext(), channelId);
//        } else {
//            builder = new Notification.Builder(getApplicationContext());
//            builder.setSound(null);
//        }
//        builder.setSmallIcon(R.mipmap.ic_logo)
//                .setContentTitle("云物流")
//                .setContentText("正在后台运行")
//                .setWhen(System.currentTimeMillis());
//
//        if (android.os.Build.VERSION.SDK_INT >= 16) {
//            notification = builder.build();
//        } else {
//            return builder.getNotification();
//        }
//        return notification;

        Notification notification = null;
        //设置后台定位
        //android8.0及以上使用NotificationUtils
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationUtils notificationUtils = new NotificationUtils(this);
            Notification.Builder builder = notificationUtils.getAndroidChannelNotification
                    ("云物流", "正在后台定位");
            notification = builder.build();
        } else {
            //获取一个Notification构造器
            Notification.Builder builder = new Notification.Builder(MainActivity.this);
            Intent nfIntent = new Intent(MainActivity.this, MainActivity.class);

            builder.setContentIntent(PendingIntent.
                            getActivity(MainActivity.this, 0, nfIntent, 0)) // 设置PendingIntent
                    .setContentTitle("云物流") // 设置下拉列表里的标题
                    .setSmallIcon(R.mipmap.ic_logo) // 设置状态栏内的小图标
                    .setContentText("正在后台定位") // 设置上下文内容
                    .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

            notification = builder.build(); // 获取构建好的Notification
        }
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音

        // 将定位SDK的SERVICE设置成为前台服务, 提高定位进程存活率
        mlocationClient.enableLocInForeground(1, notification);

        return notification;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mlocationClient != null) {
            // 关闭前台定位服务
            mlocationClient.disableLocInForeground(true);
            // 取消之前注册的 BDAbstractLocationListener 定位监听函数
            if (myLocationListener != null) {
                mlocationClient.unRegisterLocationListener(myLocationListener);
            }
            // 停止定位sdk
            mlocationClient.stop();

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
