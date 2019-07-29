package com.linkface.baselibrary.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * Created by yuemq on 2018/9/21.
 */

public class ClipboardUtil {

    /**
     * 复制内容到剪切板
     *
     * @param context
     * @param text
     */
    public static void copyToClipboard(Context context, String text) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(ClipData.newPlainText(null, text));
    }

    public static String getClipboardText(Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager.hasPrimaryClip()) {
            return clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
        }
        return null;
    }
}
