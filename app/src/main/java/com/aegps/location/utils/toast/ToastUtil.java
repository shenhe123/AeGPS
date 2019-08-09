package com.aegps.location.utils.toast;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.aegps.location.AeApplication;
import com.aegps.location.R;
import com.aegps.location.base.BaseApplication;


/**
 * Toast统一管理类
 */
public class ToastUtil {

    private static IToast toast;
    static final Handler UTIL_HANDLER = new Handler(Looper.getMainLooper());

    private static void initToast(CharSequence message, int duration) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            initToastInMainThread(message, duration);
        } else {
            UTIL_HANDLER.post(() -> initToastInMainThread(message, duration));
        }
    }

    private static void initToastInMainThread(CharSequence message, int duration) {
        if (TextUtils.isEmpty(message)) return;
        if (toast == null) {
            //使用默认布局
            toast = DToast.make(AeApplication.getAppContext()).setText(R.id.tv_content_default, message.toString());
        } else {
            toast.setText(R.id.tv_content_default, message.toString());
            toast.setDuration(duration);
        }
        toast.setGravity(81, 0, 128);
        toast.show();
    }

    /**
     * 短时间显示Toast
     *
     * @param message
     */
    public static void showShort(CharSequence message) {
        initToast(message, Toast.LENGTH_SHORT);
    }


    /**
     * 短时间显示Toast
     *
     * @param strResId
     */
    public static void showShort(int strResId) {
//		Toast.makeText(context, strResId, Toast.LENGTH_SHORT).show();
        initToast(BaseApplication.getAppContext().getResources().getText(strResId), Toast.LENGTH_SHORT);
    }

    /**
     * 长时间显示Toast
     *
     * @param message
     */
    public static void showLong(CharSequence message) {
        initToast(message, Toast.LENGTH_LONG);
    }

    /**
     * 长时间显示Toast
     *
     * @param strResId
     */
    public static void showLong(int strResId) {
        initToast(BaseApplication.getAppContext().getResources().getText(strResId), Toast.LENGTH_LONG);
    }

    /**
     * 自定义显示Toast时间
     *
     * @param message
     * @param duration
     */
    public static void show(CharSequence message, int duration) {
        initToast(message, duration);
    }

    /**
     * 自定义显示Toast时间
     *
     * @param strResId
     * @param duration
     */
    public static void show(int strResId, int duration) {
        initToast(BaseApplication.getAppContext().getResources().getText(strResId), duration);
    }
}
