package com.aegps.location.widget.popupwindow;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

/**
 * Created by zhouyibo on 2018/1/22.
 * popupwindow 控制器
 */

public abstract class BasePWControl {
    protected PopupWindow mPopupWindow;
    protected View mView;
    protected Context mContext;

    public PopupWindow getPopupWindow() {
        return mPopupWindow;
    }

    public void setPopupWindow(PopupWindow popupWindow) {
        mPopupWindow = popupWindow;
    }

    public BasePWControl(Context context, ViewGroup layoutParent) {
        mContext = context;
        mView = ((Activity) context).getLayoutInflater().inflate(injectLayout(), layoutParent, false);
        mPopupWindow = new PopupWindow(mView, injectParamsWight(), injectParamsHeight(),true);
        mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        mPopupWindow.setBackgroundDrawable(null);
        injectBackDrawable();
//        injectOutsideTouchable();
        mPopupWindow.setOnDismissListener(this::dismissEnd);
        if (injectAnimationStyle() != -1){
            mPopupWindow.setAnimationStyle(injectAnimationStyle());
        }
        initView();
    }

//    protected void injectOutsideTouchable(){
//        mPopupWindow.setOutsideTouchable(false);
//    };

    /**
     * 控件初始化
     */
    protected abstract void initView();

    /**
     * 设置布局资源
     *
     * @return
     */
    protected abstract @LayoutRes
    int injectLayout();

    /**
     * 设置动画
     */
    protected abstract int injectAnimationStyle();

    /**
     * 设置背景图
     */
    protected  void injectBackDrawable(){
        mPopupWindow.setBackgroundDrawable(null);
    }

    /**
     * 返回popupwindow高度
     *
     * @return
     */
    public int injectParamsHeight() {
        return LinearLayout.LayoutParams.MATCH_PARENT;
    }

    /**
     * 返回popupwindow宽度
     */
    public int injectParamsWight() {
        return LinearLayout.LayoutParams.MATCH_PARENT;
    }

    /**
     * 显示位置
     */
    public void showAtLocation(View parent, int gravity, int x, int y) {
        mPopupWindow.showAtLocation(parent, gravity, x, y);
        showStart();
    }

    public void showAsDropDown(View parent, int x, int y) {
        mPopupWindow.showAsDropDown(parent, x, y);
        showStart();
    }

    /**
     * 是否正在显示
     */
    public boolean isShowing() {
        return mPopupWindow != null && mPopupWindow.isShowing();
    }

    /**
     * 消除popupwindwo
     */
    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
        dismissEnd();
    }

    /**
     * cancel
     */
    protected abstract void cancel();

    /**
     * 开始显示
     */
    protected void showStart(){
    }
    /**
     * 控件消失
     */
    protected void dismissEnd(){
    }


}
