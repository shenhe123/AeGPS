package com.aegps.location;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.aegps.location.base.BaseActivity;
import com.aegps.location.utils.GlideUtil;
import com.aegps.location.widget.CircleImageView;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity{

    // UI references.
    private CircleImageView mIvLogo;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        mIvLogo = ((CircleImageView) findViewById(R.id.iv_icon));
        mIvLogo.setImageResource(R.mipmap.ic_launcher);
    }

    public void attemptLogin(View view) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
}

