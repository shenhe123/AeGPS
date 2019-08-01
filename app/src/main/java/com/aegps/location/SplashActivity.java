package com.aegps.location;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.aegps.location.base.BaseActivity;

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
    public void initPresenter() {

    }

    @Override
    public void initView() {
        // 延迟1.5s，启动主界面
        mHandler.sendEmptyMessageDelayed(GO_HOME,1500);
    }

    private void goHomeActivity() {
        Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }
}
