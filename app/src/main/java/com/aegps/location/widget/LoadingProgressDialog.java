
package com.aegps.location.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aegps.location.R;
import com.aegps.location.utils.AnimationUtil;


public class LoadingProgressDialog {

    private static int[] mImageRess = new int[]{R.drawable.loading_1,
            R.drawable.loading_2, R.drawable.loading_3, R.drawable.loading_4};

    private static int mDuration = 500;

    private static int houseconfig_duration = 150;

    private static Dialog loadingDialog = null;

    private static SceneAnimation sceneAnimation = null;

    private static Activity mActivity = null;

    private static String mPath = "";
    private static ImageView iv;
    private static TextView tv;


    /**
     * 显示一个等待框
     *
     * @param context  上下文环境
     * @param isCancel 是否能用返回取消
     * @param isRight  true文字在右边 false在下面
     */
    public static void show(Activity context, boolean isCancel, boolean isRight) {
        mActivity = context;
        if (!mActivity.isFinishing()) { // 判断 activity 是否已经关闭
            createDialog(context, "", isCancel, isRight);
        }
    }

    /**
     * 显示一个等待框
     *
     * @param context  上下文环境
     * @param msg      等待框的文字
     * @param isCancel 是否能用返回取消
     * @param isRight  true文字在右边 false在下面
     */
    public static void show(Context context, String msg, boolean isCancel,
                            boolean isRight) {
        mActivity = (Activity) context;
        if (mActivity.isFinishing()) {
            return;
        }

        if (loadingDialog == null) {
            createDialog(context, msg, isCancel, isRight);
        } else {
            Context dialogContext = loadingDialog.getContext();
            if (dialogContext instanceof Activity && ((Activity) dialogContext).isFinishing()) {
                loadingDialog = null;
                createDialog(context, msg, isCancel, isRight);
            } else if (!isShowing()) {
                loadingDialog.show();
            }
        }
    }

    public static void setImageGif(String path) {
        mPath = path;
    }

    private static void createDialog(Context context, String msg,
                                     boolean isCancel, boolean isRight) {
        LinearLayout.LayoutParams wrap_content0 = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        LinearLayout main = new LinearLayout(context);
        main.setGravity(Gravity.CENTER);
        // creatingLoadingDialog保证在构建期间是唯一一个存在的对象
        Dialog creatingLoadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
        creatingLoadingDialog.setCancelable(isCancel);// 是否可以用返回键取消
        View loadingView = View.inflate(context, R.layout.layout_unified_dialog, null);

        iv = loadingView.findViewById(R.id.iv_tips);
        iv.setImageResource(R.mipmap.loading);
        iv.setVisibility(View.VISIBLE);
        tv = loadingView.findViewById(R.id.tv_message);
        tv.setText("正在加载...");

        //无限旋转动画
        RotateAnimation rotateAnimation = AnimationUtil.getRotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f,
                1000, Animation.RESTART, Animation.INFINITE, new LinearInterpolator());
        iv.startAnimation(rotateAnimation);

//        sceneAnimation = new SceneAnimation(iv, mImageRess, mDuration);
        main.addView(loadingView, wrap_content0);
        LinearLayout.LayoutParams fill_parent = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT);
        main.setBackgroundResource(R.drawable.loading_dialog);
        creatingLoadingDialog.setContentView(main, fill_parent);// 设置布局

        loadingDialog = creatingLoadingDialog;
        loadingDialog.show();

        // 需要为loadingDialog注册取消监听，如果不注册的话，会有可能报错
        if (loadingDialog != null)
            loadingDialog.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    LoadingProgressDialog.dismiss();
                }
            });
    }

    public static void dismiss() {
        try {
            if (loadingDialog != null && mActivity != null
                    && !mActivity.isFinishing()) {
                loadingDialog.dismiss();
                if (sceneAnimation != null) {
                    sceneAnimation.removeCallBacks();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mActivity = null;
            loadingDialog = null;
            sceneAnimation = null;
        }
    }

    public static boolean isShowing() {
        return loadingDialog.isShowing();
    }

    public static Dialog getDialog() {
        return loadingDialog;
    }


}
