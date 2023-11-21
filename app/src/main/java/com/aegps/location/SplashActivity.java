package com.aegps.location;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;

import com.aegps.location.base.BaseActivity;
import com.aegps.location.utils.toast.ToastUtil;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;

import java.util.List;

import kotlin.Unit;
import se.warting.permissionsui.backgroundlocation.PermissionsUiContracts;

/** 欢迎界面
 *
 * Created by shenhe on 2019/7/30.
 */

public class SplashActivity extends BaseActivity {
    private static final int GO_HOME = 0;

    @SuppressLint("SetTextI18n")
    ActivityResultLauncher<Unit> mGetContent = registerForActivityResult(
            new PermissionsUiContracts.RequestBackgroundLocation(),
            success -> {
                setMessage();
            });

    private void setMessage() {
        mHandler.sendEmptyMessageDelayed(GO_HOME,1500);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == GO_HOME){
                goHomeActivity();
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void initData() {
        checkPermissions(needPermissions);
    }

    @Override
    public void initView() {
        // 避免从桌面启动程序后，会重新实例化入口类的activity
        Intent intent = getIntent();
        if (!isTaskRoot()
                && intent != null
                && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
                && intent.getAction() != null
                && intent.getAction().equals(Intent.ACTION_MAIN)) {
            finish();
        }
    }

    /**
     *
     * @param permissions
     * @since 2.5.0
     *
     */
    @SuppressLint("CheckResult")
    private void checkPermissions(String... permissions) {
        XXPermissions.with(this)
                // 申请多个权限
                .permission(permissions)
                // 设置权限请求拦截器（局部设置）
                //.interceptor(new PermissionInterceptor())
                // 设置不触发错误检测机制（局部设置）
                //.unchecked()
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        if (!allGranted) {
                            ToastUtil.show("获取部分权限成功，但部分权限未正常授予");
                            return;
                        }
                        //后台权限不能和其他权限一起申请
                        mGetContent.launch(null);
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
//                        if (doNotAskAgain) {
//                            toast("被永久拒绝授权，请手动授予录音和日历权限");
//                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
//                            XXPermissions.startPermissionActivity(context, permissions);
//                        } else {
//                            toast("获取录音和日历权限失败");
//                        }
                    }
                });
    }

    private void goHomeActivity() {
        Intent intent = new Intent(SplashActivity.this,RemoteLoginActivity.class);
        startActivity(intent);
        finish();
    }
}
