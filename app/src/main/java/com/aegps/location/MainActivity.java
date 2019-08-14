package com.aegps.location;

import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aegps.location.api.network.Callback;
import com.aegps.location.api.tool.SoapUtil;
import com.aegps.location.base.BaseActivity;
import com.aegps.location.bean.event.CommonEvent;
import com.aegps.location.bean.net.RefreshMonitor;
import com.aegps.location.receiver.ScreenReceiverUtil;
import com.aegps.location.service.DaemonService;
import com.aegps.location.service.PlayerMusicService;
import com.aegps.location.utils.AppManager;
import com.aegps.location.utils.ApplicationUtil;
import com.aegps.location.utils.Contants;
import com.aegps.location.utils.JobSchedulerManager;
import com.aegps.location.utils.ScreenManager;
import com.aegps.location.utils.SharedPrefUtils;
import com.aegps.location.utils.ThreadManager;
import com.aegps.location.utils.toast.ToastUtil;
import com.aegps.location.widget.CustomView;
import com.aegps.location.widget.dialog.ExitAppDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

    // 动态注册锁屏等广播
    private ScreenReceiverUtil mScreenListener;
    // 1像素Activity管理类
    private ScreenManager mScreenManager;
    // JobService，执行系统任务
    private JobSchedulerManager mJobManager;

    private ScreenReceiverUtil.SreenStateListener mScreenListenerer = new ScreenReceiverUtil.SreenStateListener() {
        @Override
        public void onSreenOn() {
            // 亮屏，移除"1像素"
            mScreenManager.finishActivity();
        }

        @Override
        public void onSreenOff() {
            // 接到锁屏广播，将SportsActivity切换到可见模式
            // "咕咚"、"乐动力"、"悦动圈"就是这么做滴
//            Intent intent = new Intent(MainActivity.this,MainActivity.class);
//            startActivity(intent);
            // 如果你觉得，直接跳出SportActivity很不爽
            // 那么，我们就制造个"1像素"惨案
            mScreenManager.startActivity();
        }

        @Override
        public void onUserPresent() {
            // 解锁，暂不用，保留
        }
    };
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
    private CustomView mFreightOrderNumber;
    private CustomView mClient;
    private CustomView mAddress;
    private CustomView mCity;
    private CustomView mContact;
    private CustomView mPhone;
    private CustomView mTel;
    private CustomView mFreightReceiptTime;
    private CustomView mFreightDrivingDistance;
    private CustomView mRemark;
    private ImageView mIvLoadingBegin;
    private TextView mTvLoadingBegin;
    private ImageView mIvUnloadReceipt;
    private TextView mTvUnloadReceipt;
    private ImageView mIvRefresh;
    private Timer mRunTimer;
    private Animation operatingAnim;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
        refreshMonitor();
    }

    private void refreshMonitor() {
        ThreadManager.getThreadPollProxy().execute(() -> SoapUtil.getInstance().refreshMonitor(ApplicationUtil.getIMEI(), SharedPrefUtils.getString(Contants.SP_DATABASE_NAME), new Callback() {
            @Override
            public void onResponse(boolean success, String data) {
                if (success) {
                    RefreshMonitor refreshMonitor = SoapUtil.getGson().fromJson(data, RefreshMonitor.class);
                    if (refreshMonitor == null) return;
                    List<RefreshMonitor.MonitorHeaderTableBean> monitorHeaderTable = refreshMonitor.getMonitorHeaderTable();
                    if (monitorHeaderTable != null && monitorHeaderTable.size() > 0) {
                        RefreshMonitor.MonitorHeaderTableBean monitorHeaderTableBean = monitorHeaderTable.get(0);
                        runOnUiThread(() -> {
                            refreshHeaderView(monitorHeaderTableBean);
                            if (monitorHeaderTableBean.getTrafficMainID() != 0) {
                                // 启动前台Service
                                startDaemonService();
                                // 启动播放音乐Service
                                startPlayMusicService();
                            }
                        });

                    }
                    List<RefreshMonitor.MonitorEntryTableBean> monitorEntryTable = refreshMonitor.getMonitorEntryTable();
                    if (monitorEntryTable != null && monitorEntryTable.size() > 0) {
                        RefreshMonitor.MonitorEntryTableBean monitorEntryTableBean = monitorEntryTable.get(0);
                        runOnUiThread(() -> {
                            refreshEntryView(monitorEntryTableBean);
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
        // 1. 注册锁屏广播监听器
        mScreenListener = new ScreenReceiverUtil(this);
        mScreenManager = ScreenManager.getScreenManagerInstance(this);
        mScreenListener.setScreenReceiverListener(mScreenListenerer);
        // 2. 启动系统任务
        mJobManager = JobSchedulerManager.getJobSchedulerInstance(this);
        mJobManager.startJobScheduler();

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
        mTransportId = (CustomView) findViewById(R.id.transport_id);
        mCarNum = (CustomView) findViewById(R.id.car_num);
        mFreightRate = (CustomView) findViewById(R.id.freight_rate);
        mBeginTime = (CustomView) findViewById(R.id.begin_time);
        mDrivingTime = (CustomView) findViewById(R.id.driving_time);
        mDrivingDistance = (CustomView) findViewById(R.id.driving_distance);
        mFreightOrderNumber = (CustomView) findViewById(R.id.freight_order_number);
        mClient = (CustomView) findViewById(R.id.client);
        mAddress = (CustomView) findViewById(R.id.address);
        mCity = (CustomView) findViewById(R.id.city);
        mContact = (CustomView) findViewById(R.id.contact);
        mPhone = (CustomView) findViewById(R.id.phone);
        mTel = (CustomView) findViewById(R.id.tel);
        mFreightReceiptTime = (CustomView) findViewById(R.id.freight_receipt_time);
        mFreightDrivingDistance = (CustomView) findViewById(R.id.freight_driving_distance);
        mRemark = (CustomView) findViewById(R.id.remark);
    }

    private void refreshEntryView(RefreshMonitor.MonitorEntryTableBean item) {
        mFreightOrderNumber.setRightText(item.getExpressCode() == null ? "" : item.getExpressCode());
        mClient.setRightText(item.getBaccName() == null ? "" : item.getBaccName());
        mAddress.setRightText(item.getDeliveryAddress() == null ? "" : item.getDeliveryAddress());
        mCity.setRightText(item.getDeliveryCity() == null ? "" : item.getDeliveryCity());
        mContact.setRightText(item.getContactPerson() == null ? "" : item.getContactPerson());
        mPhone.setRightText(item.getMobileTeleCode() == null ? "" : item.getMobileTeleCode());
        mTel.setRightText(item.getTelephoneCode() == null ? "" : item.getTelephoneCode());
        mFreightReceiptTime.setRightText(item.getEndingTime() == null ? "" : item.getEndingTime());
        mFreightDrivingDistance.setRightText(item.getMileageMeasure() + "公里");
        mRemark.setRightText(item.getRemarkSub() == null ? "" : item.getRemarkSub());
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
                // 启动前台Service
                startDaemonService();
                // 启动播放音乐Service
                startPlayMusicService();
                startRunTimer();
                break;
            case EasyCaptureActivity.EXTRA_UNLOAD_RECEIPT_CODE:
                ToastUtil.show("卸货成功");
                //关闭前台Service
                stopDaemonService();
                //关闭启动播放音乐Service
                stopPlayMusicService();
                resetView();
                stopRunTimer();
                break;
            case EasyCaptureActivity.EXTRA_TRANSPORT_CHANGE_CODE:
                ToastUtil.show("变更运输成功");
                startRunTimer();
                break;
        }
    }

    private void resetView() {
        /**
         * 卸货不可用，载货可用
         */
        resetLoadingBeginEnable();

        mTransportId.setRightText("");
        mCarNum.setRightText("");
        mFreightRate.setRightText("");
        mBeginTime.setRightText("");
        mDrivingTime.setRightText("");
        mDrivingDistance.setRightText("");
        mFreightOrderNumber.setRightText("");
        mClient.setRightText("");
        mAddress.setRightText("");
        mCity.setRightText("");
        mContact.setRightText("");
        mPhone.setRightText("");
        mTel.setRightText("");
        mFreightReceiptTime.setRightText("");
        mFreightDrivingDistance.setRightText("");
        mRemark.setRightText("");
    }

    private void resetLoadingBeginEnable() {
        //卸货签收不可用
        mLayoutUnloadReceipt.setClickable(false);
        mIvUnloadReceipt.setImageResource(R.drawable.ic_unload_receipt);
        mTvUnloadReceipt.setTextColor(getResources().getColor(R.color.color_bbbbbb));
        //载货启动可用
        mLayoutLoadingBegin.setClickable(true);
        mIvLoadingBegin.setImageResource(R.drawable.ic_load_start);
        mTvLoadingBegin.setTextColor(getResources().getColor(R.color.color_ff7c41));
        //刷新可用
        mIvRefresh.setClickable(true);
        mIvRefresh.setImageResource(R.drawable.ic_refresh_enable);
    }

    private void resetUnloadReceiptEnable() {
        //卸货签收可用
        mLayoutUnloadReceipt.setClickable(true);
        mIvUnloadReceipt.setImageResource(R.drawable.ic_unload_receipt);
        mTvUnloadReceipt.setTextColor(getResources().getColor(R.color.color_ff7c41));
        //载货启动不可用
        mLayoutLoadingBegin.setClickable(false);
        mIvLoadingBegin.setImageResource(R.drawable.ic_load_start);
        mTvLoadingBegin.setTextColor(getResources().getColor(R.color.color_bbbbbb));
        //刷新不可用
        mIvRefresh.setClickable(false);
        mIvRefresh.setImageResource(R.drawable.ic_refresh_disable);
    }

    private void stopPlayMusicService() {
        Intent intent = new Intent(MainActivity.this, PlayerMusicService.class);
        stopService(intent);
    }

    private void startPlayMusicService() {
        Intent intent = new Intent(MainActivity.this, PlayerMusicService.class);
        startService(intent);
    }

    private void startDaemonService() {
        Intent intent = new Intent(MainActivity.this, DaemonService.class);
        startService(intent);
    }

    private void stopDaemonService() {
        Intent intent = new Intent(MainActivity.this, DaemonService.class);
        stopService(intent);
    }



    private void startRunTimer() {
        TimerTask mTask = new TimerTask() {
            @Override
            public void run() {
                refreshMonitor();
            }
        };
        mRunTimer = new Timer();
        // 每隔1s更新一下时间
        mRunTimer.schedule(mTask, 1000, 1000 * 60 * 10);
    }

    private void stopRunTimer() {
        if (mRunTimer != null) {
            mRunTimer.cancel();
            mRunTimer = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 禁用返回键
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            ExitAppDialog exitAppDialog = new ExitAppDialog(MainActivity.this);
            exitAppDialog.setLiftButtonListener(dialog -> {
                //关闭前台Service
                stopDaemonService();
                //关闭启动播放音乐Service
                stopPlayMusicService();
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
    protected void onDestroy() {
        super.onDestroy();
        if (Contants.DEBUG)
            Log.d(TAG, "--->onDestroy");
        EventBus.getDefault().unregister(this);
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
}
