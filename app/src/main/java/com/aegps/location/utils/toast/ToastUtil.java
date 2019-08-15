package com.aegps.location.utils.toast;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.aegps.location.AeApplication;
import com.aegps.location.R;
import com.aegps.location.base.BaseApplication;
import com.aegps.location.utils.LogUtil;


/**
 * Toast统一管理类
 */
public class ToastUtil {

    private static IToast toast;
    static final Handler UTIL_HANDLER = new Handler(Looper.getMainLooper());

    private static void initToast(CharSequence message) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            initToastInMainThread(message);
        } else {
            UTIL_HANDLER.post(() -> initToastInMainThread(message));
        }
    }

    private static void initToastInMainThread(CharSequence message) {
        if (TextUtils.isEmpty(message)) return;
        if (toast == null) {
            //使用默认布局
            toast = DToast.make(AeApplication.getAppContext()).setText(R.id.tv_content_default, message.toString());
        } else {
            toast.setText(R.id.tv_content_default, message.toString());
        }
        toast.setGravity(81, 0, 128);
        toast.show();
    }

    /**
     * 短时间显示Toast
     *
     * @param message
     */
    public static void show(CharSequence message) {
        initToast(message);
    }


    /**
     * 短时间显示Toast
     *
     * @param strResId
     */
    public static void show(int strResId) {
        initToast(BaseApplication.getAppContext().getResources().getText(strResId));
    }
}
