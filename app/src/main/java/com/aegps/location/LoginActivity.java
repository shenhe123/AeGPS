package com.aegps.location;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aegps.location.bean.ReturnTableResult;
import com.aegps.location.bean.SysDataTableList;
import com.aegps.location.api.network.Callback;
import com.aegps.location.api.tool.SoapUtil;
import com.aegps.location.base.BaseActivity;
import com.aegps.location.utils.Contants;
import com.aegps.location.utils.LogUtil;
import com.aegps.location.utils.SharedPrefUtils;
import com.aegps.location.utils.ThreadManager;
import com.aegps.location.utils.WindowStatusHelp;
import com.aegps.location.widget.CircleImageView;
import com.aegps.location.widget.popupwindow.account.AccountMenuWindow;
import com.google.gson.Gson;

import org.ksoap2.SoapEnvelope;

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
        mTvAccount = ((TextView) findViewById(R.id.tv_account));
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
                protected void selectAccount(String accountName) {
                    mTvAccount.setText(accountName);
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
        ThreadManager.getThreadPollProxy().execute(() -> SoapUtil.getInstance().getAccountData("GetJsonData", item, new Callback() {
            @Override
            public void onResponse(SoapEnvelope envelope) {
                LogUtil.d("result:--->" + envelope.bodyIn.toString());
                if (TextUtils.isEmpty(envelope.bodyIn.toString())) return;
                String[] bodyArray = envelope.bodyIn.toString().split(";");
                if (bodyArray.length > 2) {
                    String sJsonOutData = bodyArray[1];
                    String returnJson = sJsonOutData.replaceFirst("sJsonOutData=", "");
                    //保存账套信息
                    SharedPrefUtils.saveString(Contants.SP_ACCOUNT_LIST, returnJson);

                    ReturnTableResult returnTableResult = new Gson().fromJson(returnJson, ReturnTableResult.class);
                    returnTable = returnTableResult.getReturnTable();
                }
            }

            @Override
            public void onFailure(Object o) {
                LogUtil.d("result failure:--->" + o.toString());
            }
        }));

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

