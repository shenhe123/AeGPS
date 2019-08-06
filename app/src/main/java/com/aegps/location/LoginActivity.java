package com.aegps.location;

import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.aegps.location.api.module.SysDataTableList;
import com.aegps.location.api.network.Callback;
import com.aegps.location.api.tool.SoapEnvelopeUtil;
import com.aegps.location.api.tool.SoapUtil;
import com.aegps.location.base.BaseActivity;
import com.aegps.location.utils.LogUtil;
import com.aegps.location.utils.ThreadManager;
import com.aegps.location.utils.WindowStatusHelp;
import com.aegps.location.widget.CircleImageView;
import com.aegps.location.widget.popupwindow.account.AccountMenuWindow;

import org.ksoap2.SoapEnvelope;

import java.util.ArrayList;
import java.util.List;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    // UI references.
    private CircleImageView mIvLogo;
    private EditText mTvAccount;
    private AccountMenuWindow mAccountWindow;
    private LinearLayout mLayoutParent;

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
        mLayoutParent = ((LinearLayout) findViewById(R.id.layout_parent));
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

    /**
     * 展示账套弹窗
     */
    private void showPopupWindow() {
        if (this.isFinishing()) {
            return;
        }
        if (mAccountWindow == null) {
            initCommentWindow();
        }
        mAccountWindow.setContent(new ArrayList<String>());
        mAccountWindow.showAtLocation(mLayoutParent, Gravity.BOTTOM, 0, 0);
        WindowStatusHelp.setWindowAlpha(this, 0.5f);
    }

    private void dismissCommentWindow() {
        WindowStatusHelp.setWindowAlpha(this, 1);
        if (mAccountWindow != null && mAccountWindow.isShowing()) {
            mAccountWindow.dismiss();
            mAccountWindow = null;
        }
    }

    private void initCommentWindow() {
        if (mAccountWindow == null) {
            mAccountWindow = new AccountMenuWindow(mContext, mLayoutParent) {

                @Override
                protected void selectAccount(String accountName) {

                }

                @Override
                protected void cancel() {
                    dismissCommentWindow();
                }

                @Override
                protected void dismissEnd() {
                    super.dismissEnd();
                    dismissCommentWindow();
                }
            };
        }
    }

    private void getAccountData() {
        SysDataTableList.SysDataTable item = new SysDataTableList.SysDataTable("0",
                "Plat_GetCountingRoomName",
                "",
                "07",
                "",
                "",
                "");
        ThreadManager.getThreadPollProxy().execute(new Runnable() {
            @Override
            public void run() {
                SoapUtil.getInstance().getAccountData("GetJsonData", item, new Callback() {
                    @Override
                    public void onResponse(SoapEnvelope envelope) {
                        LogUtil.d("result:--->" + envelope.bodyIn.toString());
                        String text = SoapEnvelopeUtil.getTextFromResponse(envelope);
                        LogUtil.d("result-text:--->" + text);
                    }

                    @Override
                    public void onFailure(Object o) {
                        LogUtil.d("result failure:--->" + o.toString());
                    }
                });
            }
        });
//        ThreadManager.getThreadPollProxy().execute(new Runnable() {
//            @Override
//            public void run() {
//                String namesapce = "http://tempuri.org/";
//                String soapAction = "http://tempuri.org/ITradingService/GetJsonData";
//                String method = "GetJsonData";
//                SoapObject request = new SoapObject("http://tempuri.org/", "GetJsonData");
//                request.addProperty("sJsonInData", "{\"SysDataTableList\":[{\"HandleType\":\"0\",\"HandleCode\":\"Plat_GetCountingRoomName\",\"UserCode\":\"\",\"PlatCode\":\"07\",\"DataBaseName\":\"\",\"ClientIP\":\"\",\"FunctionID\":\"\"}]}");
//                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//
//                // 下面这两句是一样的作用，写一句就行了
//                envelope.bodyOut = request;
//                envelope.setOutputSoapObject(request);
//
//                // 设置是否调用的是dotNet开发的WebService
//                envelope.dotNet = true;
//
//                HttpTransportSE transport = new HttpTransportSE("http://182.92.191.17:8800/TradingService.svc");
//                try {
//                    // 调用
//                    transport.call(soapAction, envelope);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                // 获取返回的数据
//                SoapObject object = (SoapObject) envelope.bodyIn;
//                if(null==object){
//                    return;
//                }
//                // 获取返回的结果
//                String result = object.getProperty(0).toString();
//                String data = object.getProperty(1).toString();
//
//                LogUtil.d(result);
//            }
//        });

    }
//
//    /**
//     * @param keyCode
//     * @param event
//     * @return
//     */
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}

