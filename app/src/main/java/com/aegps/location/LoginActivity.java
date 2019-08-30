package com.aegps.location;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aegps.location.bean.net.MobileVehicleResult;
import com.aegps.location.bean.net.ReturnTable;
import com.aegps.location.bean.net.ReturnTableResult;
import com.aegps.location.api.network.Callback;
import com.aegps.location.api.tool.SoapUtil;
import com.aegps.location.base.BaseActivity;
import com.aegps.location.update.UpdateAppHelper;
import com.aegps.location.utils.ApplicationUtil;
import com.aegps.location.utils.Contants;
import com.aegps.location.utils.SharedPrefUtils;
import com.aegps.location.utils.ThreadManager;
import com.aegps.location.utils.toast.ToastUtil;
import com.aegps.location.utils.WindowStatusHelp;
import com.aegps.location.widget.CircleImageView;
import com.aegps.location.widget.popupwindow.login.DataBaseMenuWindow;

import java.util.ArrayList;
import java.util.List;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    // UI references.
    private CircleImageView mIvLogo;
    private TextView mTvAccount;
    private DataBaseMenuWindow mAccountWindow;
    private LinearLayout mLayoutParent;
    private List<ReturnTableResult.ReturnTableBean> returnTable = new ArrayList<>();
    private EditText mEtCarId;
    private String mDatabaseName;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                getDataBase();
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initData() {
        UpdateAppHelper.checkCommonUpdate(LoginActivity.this);
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
            ToastUtil.show("账号密码不允许为空");
            return;
        }
        if (TextUtils.isEmpty(mDatabaseName)) {
            ToastUtil.show("账套错误");
            return;
        }
        login(ApplicationUtil.getIMEI(), mEtCarId.getText().toString(), mDatabaseName);
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

        if (returnTable == null || returnTable.size() <= 0) {
            ToastUtil.show("获取账套信息失败");
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
            mAccountWindow = new DataBaseMenuWindow(mContext, mLayoutParent) {

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
        startProgressDialog();
        ThreadManager.getThreadPollProxy().execute(() -> SoapUtil.getInstance().getDataBase(new Callback() {

            @Override
            public void onResponse(boolean success, String data) {
                if (success) {
                    ReturnTableResult returnTableResult = SoapUtil.getGson().fromJson(data, ReturnTableResult.class);
                    if (returnTableResult == null) return;
                    returnTable = returnTableResult.getReturnTable();
                    //保存账套信息
                    SharedPrefUtils.saveString(Contants.SP_ACCOUNT_LIST, data);
                } else {
                    SoapUtil.onFailure(data);
                }
            }

            @Override
            public void onFailure(Object o) {
                ToastUtil.show(o.toString());
            }

            @Override
            public void onMustRun() {
                stopProgressDialog();
            }
        }));

    }

    /**
     * 获取手机默认关联车牌号
     */
    private void getPhoneRelateCarIdData() {
        startProgressDialog();
        ThreadManager.getThreadPollProxy().execute(() -> SoapUtil.getInstance().getMobileVehicle(mDatabaseName, ApplicationUtil.getIMEI(), new Callback() {
            @Override
            public void onResponse(boolean success, String data) {
                if (success) {
                    MobileVehicleResult mobileVehicleResult = SoapUtil.getGson().fromJson(data, MobileVehicleResult.class);
                    if (mobileVehicleResult == null) return;
                    List<MobileVehicleResult.ReturnTableBean> returnTable = mobileVehicleResult.getReturnTable();
                    if (returnTable == null || returnTable.size() <= 0) return;
                    MobileVehicleResult.ReturnTableBean returnTableBean = returnTable.get(0);
                    runOnUiThread(() -> mEtCarId.setText(returnTableBean.getVehicleCode()));
                } else {
                    SoapUtil.onFailure(data);
                }
            }

            @Override
            public void onFailure(Object o) {
                ToastUtil.show(o.toString());
            }

            @Override
            public void onMustRun() {
                stopProgressDialog();
            }
        }));

    }

    private void login(String userCode, String password, String dataName) {
        startProgressDialog("正在登录...");
        ThreadManager.getThreadPollProxy().execute(() -> SoapUtil.getInstance().login(userCode, password, dataName, new Callback() {
            @Override
            public void onResponse(boolean success, String data) {
                if (success) {
                    ReturnTable returnTable = SoapUtil.getGson().fromJson(data, ReturnTable.class);
                    if (returnTable != null && returnTable.getReturnTable() != null && returnTable.getReturnTable().size() > 0) {
                        ReturnTable.ReturnTableBean returnTableBean = returnTable.getReturnTable().get(0);
                        if (returnTableBean != null) {
                            SharedPrefUtils.saveInt(Contants.SP_UPLOAD_INTERVAL_DURATION, returnTableBean.getIntervalDuration());
                        }
                    }
                    SharedPrefUtils.saveString(Contants.SP_DATABASE_NAME, dataName);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    SoapUtil.onFailure(data);
                }
            }

            @Override
            public void onFailure(Object o) {
                ToastUtil.show(o.toString());
            }

            @Override
            public void onMustRun() {
                stopProgressDialog();
            }
        }));
    }
}

