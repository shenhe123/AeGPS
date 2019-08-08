package com.aegps.location;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.aegps.location.api.network.Callback;
import com.aegps.location.api.network.SoapCall;
import com.aegps.location.api.tool.SoapUtil;
import com.aegps.location.bean.event.CommonEvent;
import com.aegps.location.bean.net.CommonReturnInfoTable;
import com.aegps.location.utils.Contants;
import com.aegps.location.utils.LogUtil;
import com.aegps.location.utils.SharedPrefUtils;
import com.aegps.location.utils.ThreadManager;
import com.aegps.location.utils.ToastUtil;
import com.aegps.location.zxing.CaptureActivity;
import com.aegps.location.zxing.DecodeFormatManager;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;

public class EasyCaptureActivity extends CaptureActivity {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_SHIPMENTBAR_CODE = "extra_shipmentbar_code";
    public static final String EXTRA_CODE = "extra_code";
    public static final int EXTRA_LOAD_BEGIN_CODE = 1;
    public static final int EXTRA_TRANSPORT_CHANGE_CODE = 3;

    private String title = "二维码扫描";
    private int code = -1;
    private String shipmentBarCode;

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
            shipmentBarCode = getIntent().getStringExtra(EXTRA_SHIPMENTBAR_CODE);
            code = getIntent().getIntExtra(EXTRA_CODE, -1);
        }
        tvTitle.setText(title);
        getCaptureHelper()
//                .decodeFormats(DecodeFormatManager.QR_CODE_FORMATS)//设置只识别二维码会提升速度
                .playBeep(true)
                .vibrate(true);
    }

    public void onClick(View v){
        switch (v.getId()){
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
            case EXTRA_TRANSPORT_CHANGE_CODE://运输变更
                transportChange(result);
                break;
        }
        return super.onResultCallback(result);
    }

    private void loadBegin(String trafficBarCode) {
        ThreadManager.getThreadPollProxy().execute(() -> SoapUtil.getInstance().loadBegin("1234567890", SharedPrefUtils.getString(Contants.SP_DATABASE_NAME), trafficBarCode, new Callback() {
            @Override
            public void onResponse(SoapEnvelope envelope) {
                // 获取返回的数据
                SoapObject object = (SoapObject) envelope.bodyIn;
                if (null == object) {
                    return;
                }
                LogUtil.d("envelope.bodyIn:--->" + envelope.bodyIn.toString());
                // 获取返回的结果
                String result = object.getProperty(0).toString();
                String data = object.getProperty(1).toString();
                LogUtil.d("result:--->" + result);
                LogUtil.d("data:--->" + data);
                EventBus.getDefault().post(new CommonEvent(EXTRA_LOAD_BEGIN_CODE, ""));
            }

            @Override
            public void onFailure(Object o) {
                LogUtil.e("result failure:--->" + o.toString());
            }
        }));
    }

    private void transportChange(String trafficBarCode) {
        ThreadManager.getThreadPollProxy().execute(() -> SoapUtil.getInstance().changeCarriage("1234567890", SharedPrefUtils.getString(Contants.SP_DATABASE_NAME), trafficBarCode, new Callback() {
            @Override
            public void onResponse(SoapEnvelope envelope) {
                // 获取返回的数据
                SoapObject object = (SoapObject) envelope.bodyIn;
                if (null == object) {
                    return;
                }
                LogUtil.d("envelope.bodyIn:--->" + envelope.bodyIn.toString());
                // 获取返回的结果
                String result = object.getProperty(0).toString();
                String data = object.getProperty(1).toString();
                LogUtil.d("result:--->" + result);
                LogUtil.d("data:--->" + data);
                EventBus.getDefault().post(new CommonEvent(EXTRA_TRANSPORT_CHANGE_CODE, ""));
            }

            @Override
            public void onFailure(Object o) {
                LogUtil.e("result failure:--->" + o.toString());
            }
        }));
    }
}
