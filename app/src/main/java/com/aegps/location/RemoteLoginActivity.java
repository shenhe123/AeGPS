package com.aegps.location;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aegps.location.api.network.Callback;
import com.aegps.location.api.tool.SoapUtil;
import com.aegps.location.base.BaseActivity;
import com.aegps.location.bean.net.RemoteLoginResult;
import com.aegps.location.bean.net.RemoteLoginUrlResult;
import com.aegps.location.utils.ThreadManager;
import com.aegps.location.utils.WindowStatusHelp;
import com.aegps.location.utils.toast.ToastUtil;
import com.aegps.location.widget.CircleImageView;
import com.aegps.location.widget.popupwindow.login.CompanyMenuWindow;

import java.util.ArrayList;
import java.util.List;


/**
 * A login screen that offers login via email/password.
 */
public class RemoteLoginActivity extends BaseActivity implements View.OnClickListener {

    // UI references.
    private CircleImageView mIvLogo;
//    private TextView mTvAccount;
    private CompanyMenuWindow mCompanyMenuWindwo;
    private LinearLayout mLayoutParent;
    private List<RemoteLoginResult.ReturnTableBean> returnTable = new ArrayList<>();
    private TextView mTvCompanyName;
    private String mLogonUser;
    private String mCutomerCode;

    @Override
    public int getLayoutId() {
        return R.layout.activity_remote_login;
    }

    @Override
    public void initData() {
        requestRemoteCompany();
    }

    private void requestRemoteCompany() {
        startProgressDialog();
        ThreadManager.getThreadPollProxy().execute(() -> SoapUtil.getInstance().requestRemotelogin(new Callback() {
            @Override
            public void onResponse(boolean success, String data) {
                if (success) {
                    RemoteLoginResult remoteLoginResult = SoapUtil.getGson().fromJson(data, RemoteLoginResult.class);
                    if (remoteLoginResult == null) return;
                    returnTable = remoteLoginResult.getReturnTable();
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

    @Override
    public void initView() {
        mLayoutParent = ((LinearLayout) findViewById(R.id.layout_parent));
        mIvLogo = ((CircleImageView) findViewById(R.id.iv_icon));
        mIvLogo.setImageResource(R.mipmap.ic_launcher);
//        mTvAccount = ((TextView) findViewById(R.id.tv_account));
//        mTvAccount.setOnClickListener(this);
        mTvCompanyName = ((TextView) findViewById(R.id.tv_car_id));
        mTvCompanyName.setOnClickListener(this);
    }

    public void attemptLogin(View view) {
//        if (TextUtils.isEmpty(mTvAccount.getText().toString().trim()) ||
        if (TextUtils.isEmpty(mTvCompanyName.getText().toString().trim())) {
            ToastUtil.show("公司名不允许为空");
            return;
        }
        if (TextUtils.isEmpty(mCutomerCode)) {
            ToastUtil.show("远程登录失败");
            return;
        }
        next(mCutomerCode, "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_car_id:
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
        if (mCompanyMenuWindwo == null) {
            initCommentWindow();
        }
        mCompanyMenuWindwo.setContent(returnTable);
        mCompanyMenuWindwo.showAsDropDown(mTvCompanyName, 0, 0);
        WindowStatusHelp.setWindowAlpha(this, 0.5f);
    }

    private void dismissCommentWindow() {
        WindowStatusHelp.setWindowAlpha(this, 1);
        if (mCompanyMenuWindwo != null && mCompanyMenuWindwo.isShowing()) {
            mCompanyMenuWindwo.dismiss();
            mCompanyMenuWindwo = null;
        }
    }

    private void initCommentWindow() {
        if (mCompanyMenuWindwo == null) {
            mCompanyMenuWindwo = new CompanyMenuWindow(mContext, mLayoutParent) {

                @Override
                protected void selectAccount(String logonUser, String cutomerCode) {
                    mTvCompanyName.setText(logonUser);
                    mCutomerCode = cutomerCode;
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

    private void next(String cutomerCode, String logonUser) {
        startProgressDialog();
        ThreadManager.getThreadPollProxy().execute(() -> SoapUtil.getInstance().remotelogin(cutomerCode, logonUser, new Callback() {
            @Override
            public void onResponse(boolean success, String data) {
                if (success) {
                    RemoteLoginUrlResult remoteLoginUrlResult = SoapUtil.getGson().fromJson(data, RemoteLoginUrlResult.class);
                    if (remoteLoginUrlResult != null && remoteLoginUrlResult.getReturnTable() != null && remoteLoginUrlResult.getReturnTable().size() > 0) {
                        RemoteLoginUrlResult.ReturnTableBean returnTableBean = remoteLoginUrlResult.getReturnTable().get(0);
                        SoapUtil.mDomainUrl = returnTableBean.getRequestURL();
                        Intent intent = new Intent(RemoteLoginActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        ToastUtil.show("数据错误，请重试");
                    }
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

