package com.linkface.baselibrary.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by shenhe on 2018/9/18.
 */

public class FileUtils {
    private static final String AD_EXTERNAL = "ad";
    private static final String AD_EXTERNAL_IMAGE = "image";

    /**
     * sdcard
     *
     * @return
     */
    public static String getSDPath() {
        return Environment.getExternalStorageDirectory().getPath() + File.separator;
    }

    /**
     * sdcard/ad
     *
     * @return
     */
    public static String getExternalADPath() {
        String externalPath = getSDPath() + AD_EXTERNAL;
        if (!isFileExists(externalPath))
            createSDDir(externalPath);
        showPathTag(externalPath);
        return externalPath;
    }

    /**
     * sdcard/ad/image
     *
     * @return
     */
    public static String getExternalADImagePath() {
        String externalPath = getExternalADPath() + File.separator + AD_EXTERNAL_IMAGE;
        if (!isFileExists(externalPath))
            createSDDir(externalPath);
        showPathTag(externalPath);
        return externalPath;
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsFile(final File file) {
        if (file == null) return false;
        // 如果存在，是文件则返回 true，是目录则返回 false
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsDir(final File file) {
        // 如果存在，是目录则返回 true，是文件则返回 false，不存在则返回是否创建成功
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }



    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param dirPath 目录路径
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsDir(final String dirPath) {
        return createOrExistsDir(getFileByPath(dirPath));
    }

    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    public static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断SD卡上的文件夹(文件)是否存在
     *
     * @param path
     * @return
     */
    public static boolean isFileExists(String path) {
        File file = new File(path);
        return file.exists();
    }

    /**
     * 判断文件是否存在
     *
     * @param file 文件
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public static boolean isFileExists(final File file) {
        return file != null && file.exists();
    }


    /**
     * 创建文件夹
     *
     * @param path
     * @return
     */
    public static File createSDDir(String path) {
        File dir = new File(path);
        boolean result = dir.mkdirs();
        return dir;
    }

    private static void showPathTag(String path) {
        Log.i("shenhe", path, null);
    }

    public static void writeToFile(String fileName, Bitmap bitmap) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除文件夹及其目录下的子文件
     *
     * @param path
     * @return
     */
    public static boolean deleteSDDir(String path) {
        File rootFile = new File(path);
        if (rootFile.exists() && rootFile.isDirectory()) {
            File[] files = rootFile.listFiles();
            for (File file : files) {
                Log.i("zhufeng", "删除文件：" + file.getAbsolutePath() + " name:" + file.getName());
                if (file.isDirectory()) {
                    deleteSDDir(file.getPath());
                    file.delete();
                } else {
                    file.delete();
                }
            }
            return true;
        }
        return false;
    }


    /**
     * 安装apk文件
     * @param context
     * @param appFile
     * @return
     */
    public static Intent getInstallAppIntent(Context context, File appFile) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileprovider", appFile);
                intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(appFile), "application/vnd.android.package-archive");
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return intent;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
