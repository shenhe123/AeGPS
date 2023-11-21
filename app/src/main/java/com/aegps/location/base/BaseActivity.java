package com.aegps.location.base;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.aegps.location.utils.AppManager;
import com.aegps.location.utils.StatusBarUtil;
import com.aegps.location.widget.LoadingProgressDialog;

/**
 * 基类
 */

public abstract class BaseActivity extends FragmentActivity {

    public Context mContext;
    private int count;//记录开启进度条的情况 只能开一个


    /**
     * 需要进行检测的权限数组
     * 后台运行权限，不能和其他权限一起申请
     */
    protected String[] needPermissions = {
            getSDPermission(),
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.POST_NOTIFICATIONS,
    };

    public String getSDPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            return Manifest.permission.WRITE_EXTERNAL_STORAGE;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //适配状态栏颜色
        StatusBarUtil.statusBarLightMode(this, Color.WHITE);
        doBeforeSetcontentView();
        setContentView(getLayoutId());
        mContext = this;
        this.initView();
        this.initData();
    }

    /**
     * 设置layout前配置
     */
    private void doBeforeSetcontentView() {
        // 把actvity放到application栈中管理
        AppManager.getAppManager().addActivity(this);
//        // 无标题
        //android 5.x以下系统会崩溃，style中设置Theme后，requestWindowFeature可注释
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


    }

    /*********************
     * 子类实现
     *****************************/
    //获取布局文件
    public abstract int getLayoutId();

    //简单页面无需mvp就不用管此方法即可,完美兼容各种实际场景的变通
    public abstract void initData();

    //初始化view
    public abstract void initView();


    /**
     * 通过Class跳转界面
     **/
    public void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    /**
     * 通过Class跳转界面
     **/
    public void startActivityForResult(Class<?> cls, int requestCode) {
        startActivityForResult(cls, null, requestCode);
    }

    /**
     * 含有Bundle通过Class跳转界面
     **/
    public void startActivityForResult(Class<?> cls, Bundle bundle,
                                       int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * 含有Bundle通过Class跳转界面
     **/
    public void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 开启浮动加载进度条
     */
    public void startProgressDialog() {
        count++;
        if(count==1){
            LoadingProgressDialog.show(this,false, false);
        }
    }

    /**
     * 开启浮动加载进度条
     *
     * @param msg
     */
    public void startProgressDialog(String msg) {
        count++;
        if(count==1){
            LoadingProgressDialog.show(this,false, false);
        }
    }

    /**
     * 停止浮动加载进度条
     */
    public void stopProgressDialog() {
        count--;
        if(count==0){
            if (LoadingProgressDialog.getDialog() != null && LoadingProgressDialog.isShowing()) {
                LoadingProgressDialog.dismiss();
            }
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }

}
