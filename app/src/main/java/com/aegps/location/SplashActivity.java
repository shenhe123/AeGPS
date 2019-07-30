package com.aegps.location;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.aegps.location.aegps.R;

/** 欢迎界面
 *
 * Created by shenhe on 2019/7/30.
 */

public class SplashActivity extends AppCompatActivity {
    private static final int GO_HOME = 0;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == GO_HOME){
                goHomeActivity();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // 延迟1.5s，启动主界面
        mHandler.sendEmptyMessageDelayed(GO_HOME,1500);
    }

    private void goHomeActivity() {
        Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }
}
