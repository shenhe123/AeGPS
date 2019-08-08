package com.aegps.location;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aegps.location.bean.net.MobileVehicleResult;
import com.aegps.location.bean.net.ReturnTableResult;
import com.aegps.location.api.network.Callback;
import com.aegps.location.api.tool.SoapUtil;
import com.aegps.location.base.BaseActivity;
import com.aegps.location.utils.Contants;
import com.aegps.location.utils.LogUtil;
import com.aegps.location.utils.SharedPrefUtils;
import com.aegps.location.utils.ThreadManager;
import com.aegps.location.utils.ToastUtil;
import com.aegps.location.utils.WindowStatusHelp;
import com.aegps.location.widget.CircleImageView;
import com.aegps.location.widget.popupwindow.account.AccountMenuWindow;
import com.google.gson.Gson;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    // UI references.
    private CircleImageView mIvLogo;
    private TextView mTvAccount;
    private AccountMenuWindow mAccountWindow;
    private LinearLayout mLayoutParent;
    private List<ReturnTableResult.ReturnTableBean> returnTable = new ArrayList<>();
    private EditText mEtCarId;
    private String mDatabaseName;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initData() {
        getDataBase();
    }

    @Override
    public void initView() {
        mLayoutParent = ((LinearLayout) findViewById(R.id.layout_parent));
        mIvLogo = ((CircleImageView) findViewById(R.id.iv_icon));
        mIvLogo.setImageResource(R.mipmap.ic_launcher);
        mTvAccount = ((TextView) findViewById(R.id.tv_account));
        mEtCarId = ((EditText) findViewById(R.id.tv_car_id));
        mTvAccount.setOnClickListener(this);
    }

    public void attemptLogin(View view) {
        if (TextUtils.isEmpty(mTvAccount.getText().toString().trim()) ||
                TextUtils.isEmpty(mEtCarId.getText().toString().trim())) {
            ToastUtil.showShort("账号密码不允许为空");
            return;
        }
        if (TextUtils.isEmpty(mDatabaseName)) {
            ToastUtil.showShort("账套错误");
            return;
        }
        login("1234567890", mEtCarId.getText().toString(), mDatabaseName);
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
        mAccountWindow.setContent(returnTable);
        mAccountWindow.showAsDropDown(mTvAccount, 0, 0);
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
                protected void selectAccount(String accountName, String databaseName) {
                    mTvAccount.setText(accountName);
                    mDatabaseName = databaseName;
                    getPhoneRelateCarIdData();
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

    /**
     * 获取账套信息
     */
    private void getDataBase() {
        ThreadManager.getThreadPollProxy().execute(() -> SoapUtil.getInstance().getDataBase(new Callback() {
            @Override
            public void onResponse(SoapEnvelope envelope) {
                // 获取返回的数据
                SoapObject object = (SoapObject) envelope.bodyIn;
                if(null==object){
                    return;
                }
                LogUtil.d("envelope.bodyIn:--->" + envelope.bodyIn.toString());
                // 获取返回的结果
                String result = object.getProperty(0).toString();
                String data = object.getProperty(1).toString();
                LogUtil.d("result:--->" + result);
                LogUtil.d("result:--->" + data);
                ReturnTableResult returnTableResult = new Gson().fromJson(data, ReturnTableResult.class);
                returnTable = returnTableResult.getReturnTable();
                //保存账套信息
                SharedPrefUtils.saveString(Contants.SP_ACCOUNT_LIST, data);
            }

            @Override
            public void onFailure(Object o) {
                LogUtil.e("result failure:--->" + o.toString());
            }
        }));

    }

    /**
     * 获取手机默认关联车牌号
     */
    private void getPhoneRelateCarIdData() {
        ThreadManager.getThreadPollProxy().execute(() -> SoapUtil.getInstance().getMobileVehicle(mDatabaseName, "1234567890", new Callback() {
            @Override
            public void onResponse(SoapEnvelope envelope) {
                // 获取返回的数据
                SoapObject object = (SoapObject) envelope.bodyIn;
                if(null==object){
                    return;
                }
                LogUtil.d("envelope.bodyIn:--->" + envelope.bodyIn.toString());
                // 获取返回的结果
                String result = object.getProperty(0).toString();
                String data = object.getProperty(1).toString();
                LogUtil.d("result:--->" + result);
                LogUtil.d("data:--->" + data);
                MobileVehicleResult mobileVehicleResult = new Gson().fromJson(data, MobileVehicleResult.class);
                if (mobileVehicleResult == null) return;
                List<MobileVehicleResult.ReturnTableBean> returnTable = mobileVehicleResult.getReturnTable();
                if (returnTable == null || returnTable.size() <= 0) return;
                MobileVehicleResult.ReturnTableBean returnTableBean = returnTable.get(0);
                mEtCarId.setText(returnTableBean.getVehicleCode());
            }

            @Override
            public void onFailure(Object o) {
                LogUtil.e("result failure:--->" + o.toString());
            }
        }));

    }

    private void login(String userCode, String password, String dataName) {
        ThreadManager.getThreadPollProxy().execute(() -> SoapUtil.getInstance().login(userCode, password, dataName, new Callback() {
            @Override
            public void onResponse(SoapEnvelope envelope) {
                // 获取返回的数据
                SoapObject object = (SoapObject) envelope.bodyIn;
                if(null==object){
                    return;
                }
                LogUtil.d("envelope.bodyIn:--->" + envelope.bodyIn.toString());
                // 获取返回的结果
                String result = object.getProperty(0).toString();
                String data = object.getProperty(1).toString();
                LogUtil.d("result:--->" + result);
                LogUtil.d("data:--->" + data);
                SharedPrefUtils.saveString(Contants.SP_DATABASE_NAME, dataName);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Object o) {
                LogUtil.e("result failure:--->" + o.toString());
            }
        }));
    }
}

