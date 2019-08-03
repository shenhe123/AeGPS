package com.aegps.location;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.aegps.location.zxing.CaptureActivity;
import com.aegps.location.zxing.DecodeFormatManager;

public class EasyCaptureActivity extends CaptureActivity {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_CODE = "extra_code";
    public static final int EXTRA_LOAD_BEGIN_CODE = 1;
    public static final int EXTRA_UNLOAD_RECEIPT_CODE = 2;
    public static final int EXTRA_TRANSPORT_CHANGE_CODE = 3;

    private String title = "二维码扫描";

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
        }
        tvTitle.setText(title);
        getCaptureHelper()
                .decodeFormats(DecodeFormatManager.QR_CODE_FORMATS)//设置只识别二维码会提升速度
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
        
        return super.onResultCallback(result);
    }
}
