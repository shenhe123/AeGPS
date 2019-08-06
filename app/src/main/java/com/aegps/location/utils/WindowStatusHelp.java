package com.aegps.location.utils;

import android.app.Activity;
import android.view.WindowManager;

/**
 * Created by ShenHe on 2019/8/6.
 */

public class WindowStatusHelp {
    /**
     * 改变屏幕透明度
     * @param activity activity
     * @param f f
     */
    public static void setWindowAlpha(Activity activity, float f) {
        try {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.alpha = f; //0.0-1.0
            activity.getWindow().setAttributes(lp);
        } catch (Exception e) {

        }
    }
}
