package com.aegps.location;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.aegps.location.base.BaseActivity;

/**
 * 欢迎界面
 * <p>
 * Created by shenhe on 2019/7/30.
 */

public class SplashActivity extends BaseActivity {
    private static final int GO_HOME = 0;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == GO_HOME) {
                goHomeActivity();
            }
        }
    };
    private TextView mTvVersion;

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {
        // 避免从桌面启动程序后，会重新实例化入口类的activity
        Intent intent = getIntent();
        if (!isTaskRoot()
                && intent != null
                && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
                && intent.getAction() != null
                && intent.getAction().equals(Intent.ACTION_MAIN)) {
            finish();
            return;
        }
        mTvVersion = (TextView) findViewById(R.id.tv_version);
        mTvVersion.setText("v" + BuildConfig.VERSION_NAME);
        mHandler.sendEmptyMessageDelayed(GO_HOME, 1500);
    }

    private void goHomeActivity() {
        Intent intent = new Intent(SplashActivity.this, RemoteLoginActivity.class);
        startActivity(intent);
        finish();
    }
}
