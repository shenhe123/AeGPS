package com.aegps.location;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aegps.location.base.BaseActivity;
import com.aegps.location.receiver.ScreenReceiverUtil;
import com.aegps.location.service.DaemonService;
import com.aegps.location.service.PlayerMusicService;
import com.aegps.location.utils.Contants;
import com.aegps.location.utils.HwPushManager;
import com.aegps.location.utils.JobSchedulerManager;
import com.aegps.location.utils.ScreenManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 运动界面，处理各种保活逻辑
 * <p>
 * Created by shenhe on 2019/7/30.
 */

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private Timer mRunTimer;
    private boolean isRunning;
    // 动态注册锁屏等广播
    private ScreenReceiverUtil mScreenListener;
    // 1像素Activity管理类
    private ScreenManager mScreenManager;
    // JobService，执行系统任务
    private JobSchedulerManager mJobManager;
    // 华为推送管理类
    private HwPushManager mHwPushManager;

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
    private Button mBtnLoadingBegin;
    /**
     * 卸货签收
     */
    private Button mBtnUnloadReceipt;
    /**
     * 运输变更
     */
    private Button mBtnTransportChange;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        if (Contants.DEBUG)
            Log.d(TAG, "--->onCreate");
        // 1. 注册锁屏广播监听器
        mScreenListener = new ScreenReceiverUtil(this);
        mScreenManager = ScreenManager.getScreenManagerInstance(this);
        mScreenListener.setScreenReceiverListener(mScreenListenerer);
        // 2. 启动系统任务
        mJobManager = JobSchedulerManager.getJobSchedulerInstance(this);
        mJobManager.startJobScheduler();
        // 3. 华为推送保活，允许接收透传
        mHwPushManager = HwPushManager.getInstance(this);
        mHwPushManager.startRequestToken();
        mHwPushManager.isEnableReceiveNormalMsg(true);
        mHwPushManager.isEnableReceiverNotifyMsg(true);


        mBtnLoadingBegin = (Button) findViewById(R.id.btn_loading_begin);
        mBtnLoadingBegin.setOnClickListener(this);
        mBtnUnloadReceipt = (Button) findViewById(R.id.btn_unload_receipt);
        mBtnUnloadReceipt.setOnClickListener(this);
        mBtnTransportChange = (Button) findViewById(R.id.btn_transport_change);
        mBtnTransportChange.setOnClickListener(this);
    }

    public void onRunningClick(View v) {
        if (!isRunning) {
//            mBtnRun.setText("停止跑步");
            startRunTimer();
            // 3. 启动前台Service
            startDaemonService();
            // 4. 启动播放音乐Service
            startPlayMusicService();
        } else {
//            mBtnRun.setText("开始跑步");
            stopRunTimer();
            //关闭前台Service
            stopDaemonService();
            //关闭启动播放音乐Service
            stopPlayMusicService();
        }
        isRunning = !isRunning;
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 禁用返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isRunning) {
                Toast.makeText(MainActivity.this, "正在跑步", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void startRunTimer() {
        TimerTask mTask = new TimerTask() {
            @Override
            public void run() {
                // 更新UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        mTvRunTime.setText(timeHour+" : "+timeMin+" : "+timeSec);
                    }
                });
            }
        };
        mRunTimer = new Timer();
        // 每隔1s更新一下时间
        mRunTimer.schedule(mTask, 1000, 1000);
    }

    private void stopRunTimer() {
        if (mRunTimer != null) {
            mRunTimer.cancel();
            mRunTimer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Contants.DEBUG)
            Log.d(TAG, "--->onDestroy");
        stopRunTimer();
//        mScreenListener.stopScreenReceiverListener();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btn_loading_begin:
                EasyCaptureActivity.launch(this, mBtnLoadingBegin.getText().toString(), EasyCaptureActivity.EXTRA_LOAD_BEGIN_CODE);
                break;
            case R.id.btn_unload_receipt:
                EasyCaptureActivity.launch(this, mBtnUnloadReceipt.getText().toString(), EasyCaptureActivity.EXTRA_UNLOAD_RECEIPT_CODE);
                break;
            case R.id.btn_transport_change:
                EasyCaptureActivity.launch(this, mBtnTransportChange.getText().toString(), EasyCaptureActivity.EXTRA_TRANSPORT_CHANGE_CODE);
                break;
        }
    }
}
