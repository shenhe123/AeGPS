package com.aegps.location;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.aegps.location.api.module.SysDataTableItem;
import com.aegps.location.api.network.Callback;
import com.aegps.location.api.tool.SoapEnvelopeUtil;
import com.aegps.location.api.tool.SoapUtil;
import com.aegps.location.base.BaseActivity;
import com.aegps.location.utils.LogUtil;
import com.aegps.location.widget.CircleImageView;

import org.ksoap2.SoapEnvelope;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    // UI references.
    private CircleImageView mIvLogo;
    private EditText mTvAccount;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initData() {
        getAccountData();
    }

    @Override
    public void initView() {
        mIvLogo = ((CircleImageView) findViewById(R.id.iv_icon));
        mIvLogo.setImageResource(R.mipmap.ic_launcher);
        mTvAccount = ((EditText) findViewById(R.id.tv_account));
        mTvAccount.setOnClickListener(this);
    }

    public void attemptLogin(View view) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_account:
                showPopupWindow();
                break;
        }
    }

    private void showPopupWindow() {

    }

    private void getAccountData() {
        SysDataTableItem item = new SysDataTableItem("0",
                "LO_MobileTraffic_RefreshMonitor",
                "",
                "07",
                "",
                "",
                "");
        SoapUtil.getInstance().getAccountData("LO_MobileTraffic_RefreshMonitor", item, new Callback() {
            @Override
            public void onResponse(SoapEnvelope envelope) {
                LogUtil.d("result:--->" + envelope.bodyIn.toString());
                String text = SoapEnvelopeUtil.getTextFromResponse(envelope);
                LogUtil.d("result-text:--->" + text);
            }

            @Override
            public void onFailure(Object o) {
                LogUtil.d("result failure:--->");
            }
        });
    }

    /**
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

