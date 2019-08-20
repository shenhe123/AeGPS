package com.aegps.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.aegps.location.base.BaseActivity;
import com.aegps.location.utils.AppManager;
import com.aegps.location.utils.toast.ToastUtil;
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
        mHandler.sendEmptyMessageDelayed(GO_HOME,1500);
    }

    private void goHomeActivity() {
        Intent intent = new Intent(SplashActivity.this,RemoteLoginActivity.class);
        startActivity(intent);
        finish();
    }
}
