package com.aegps.location;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.aegps.location.api.network.Callback;
import com.aegps.location.api.tool.SoapUtil;
import com.aegps.location.bean.event.CommonEvent;
import com.aegps.location.utils.ApplicationUtil;
import com.aegps.location.utils.Contants;
import com.aegps.location.utils.SharedPrefUtils;
import com.aegps.location.utils.ThreadManager;
import com.aegps.location.utils.toast.ToastUtil;
import com.aegps.location.zxing.CaptureActivity;

import org.greenrobot.eventbus.EventBus;

public class EasyCaptureActivity extends CaptureActivity {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_CODE = "extra_code";
    public static final int EXTRA_LOAD_BEGIN_CODE = 1;
    public static final int EXTRA_UNLOAD_RECEIPT_CODE = 2;
    public static final int EXTRA_TRANSPORT_CHANGE_CODE = 3;

    private String title = "二维码扫描";
    private int code = -1;

    public static void launch(Context context, String title, int requstCode) {
        Intent intent = new Intent(context, EasyCaptureActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_CODE, requstCode);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_easy_capture;
    }

    @Override
    public void initView() {
        super.initView();
        TextView tvTitle = findViewById(R.id.tvTitle);
        if (getIntent() != null) {
            title = getIntent().getStringExtra(EXTRA_TITLE);
            code = getIntent().getIntExtra(EXTRA_CODE, -1);
        }
        tvTitle.setText(title);
        getCaptureHelper()
//                .decodeFormats(DecodeFormatManager.QR_CODE_FORMATS)//设置只识别二维码会提升速度
                .playBeep(true)
                .vibrate(true);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivLeft:
                onBackPressed();
                break;
        }
    }

    @Override
    public boolean onResultCallback(String result) {
        switch (code) {
            case -1:
                break;
            case EXTRA_LOAD_BEGIN_CODE://载货启动
                loadBegin(result);
                break;
            case EXTRA_UNLOAD_RECEIPT_CODE://ic_unload_receipt
                unLoadReceipt(result);
                break;
            case EXTRA_TRANSPORT_CHANGE_CODE://运输变更
                transportChange(result);
                break;
        }
        return super.onResultCallback(result);
    }

    private void loadBegin(String trafficBarCode) {
        ThreadManager.getThreadPollProxy().execute(() -> SoapUtil.getInstance().loadBegin(ApplicationUtil.getIMEI(), SharedPrefUtils.getString(Contants.SP_DATABASE_NAME), trafficBarCode,
                new Callback() {
                    @Override
                    public void onResponse(boolean success, String data) {
                        if (success) {
                            EventBus.getDefault().post(new CommonEvent(EXTRA_LOAD_BEGIN_CODE, ""));
                        } else {
                            SoapUtil.onFailure(data);
                        }
                    }

                    @Override
                    public void onFailure(Object o) {
                        ToastUtil.show(o.toString());
                    }
                }));
    }

    private void unLoadReceipt(String shipmentBarCode) {
        ThreadManager.getThreadPollProxy().execute(() -> SoapUtil.getInstance().unloadReceipt(ApplicationUtil.getIMEI(), SharedPrefUtils.getString(Contants.SP_DATABASE_NAME), shipmentBarCode, new Callback() {
            @Override
            public void onResponse(boolean success, String data) {
                if (success) {
                    EventBus.getDefault().post(new CommonEvent(EXTRA_UNLOAD_RECEIPT_CODE, ""));
                } else {
                    SoapUtil.onFailure(data);
                }
            }

            @Override
            public void onFailure(Object o) {
                ToastUtil.show(o.toString());
            }
        }));
    }

    private void transportChange(String trafficBarCode) {
        ThreadManager.getThreadPollProxy().execute(() -> SoapUtil.getInstance().changeCarriage(ApplicationUtil.getIMEI(), SharedPrefUtils.getString(Contants.SP_DATABASE_NAME), trafficBarCode,
                new Callback() {
                    @Override
                    public void onResponse(boolean success, String data) {
                        if (success) {
                            EventBus.getDefault().post(new CommonEvent(EXTRA_TRANSPORT_CHANGE_CODE, ""));
                        } else {
                            SoapUtil.onFailure(data);
                        }
                    }

                    @Override
                    public void onFailure(Object o) {
                        ToastUtil.show(o.toString());
                    }
                }));
    }
}
