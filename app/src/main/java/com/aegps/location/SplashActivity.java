package com.aegps.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.aegps.location.base.BaseActivity;
import com.aegps.location.utils.LogUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

/** 欢迎界面
 *
 * Created by shenhe on 2019/7/30.
 */

public class SplashActivity extends BaseActivity {
    private static final int GO_HOME = 0;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == GO_HOME){
                goHomeActivity();
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {
        requestPermission();
    }

    private void goHomeActivity() {
        Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @SuppressLint("CheckResult")
    private void requestPermission() {
        //同时请求多个权限
        RxPermissions rxPermission = new RxPermissions(SplashActivity.this);
        rxPermission.request(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA)
                .subscribe(granted -> {
                    if (granted) {
                        mHandler.sendEmptyMessageDelayed(GO_HOME,1500);
                    }
                });

    }
}
