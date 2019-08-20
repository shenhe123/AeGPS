package com.aegps.location.utils;

import android.content.Context;
import android.os.Environment;

import com.aegps.location.locationservice.PowerManagerUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by shenhe on 2019/8/16.
 *
 * @description
 */
public class FilteWriterUtil {

    /**
     * 追加文件：使用RandomAccessFile
     *
     * @param fileName
     *            文件名
     * @param content
     *            追加的内容
     */
    public static void wirteToLoacal(String fileName, String content) {
        File file = new File(fileName);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            FileOutputStream fos = new FileOutputStream(fileName, true);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            osw.write(content);
            osw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取根目录(应用cache 或者 SD卡)
     * <br>
     * <br>
     * 优先获取SD卡根目录[/storage/sdcard0]
     * <br>
     * <br>
     * 应用缓存目录[/data/data/应用包名/cache]
     * <br>
     *
     * @param context 上下文
     * @return
     */
    public static String getRootDir(Context context)
    {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            // 优先获取SD卡根目录[/storage/sdcard0]
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else
        {
            // 应用缓存目录[/data/data/应用包名/cache]
            return context.getCacheDir().getAbsolutePath();
        }
    }
}
