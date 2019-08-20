package com.aegps.location.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 */

public class SharedPrefUtils {
    public static final String LF_SP_ATTENDANCE = "lf_sp_advert";
    public static final String PREFERENCES_ALARM = "preferences_alarm";

    private static SharedPreferences sSharedPreferences = null;

    public static void init(Context context) {
        if (context != null) {
            sSharedPreferences = context.getSharedPreferences(LF_SP_ATTENDANCE, Activity.MODE_PRIVATE);
        }
    }

    public static void saveString(String key, String value) {
        if (sSharedPreferences != null) {
            sSharedPreferences.edit().putString(key, value).commit();
        }
    }

    public static String getString(String key) {
        String result = "";
        if (sSharedPreferences != null) {
            result = sSharedPreferences.getString(key, "");
        }
        return result;
    }

    public static String getString(String key, String defaultValue) {
        String result;
        if(sSharedPreferences != null) {
            result = sSharedPreferences.getString(key, defaultValue);
        } else {
            result = "error";
        }

        return result;
    }

    public static void remove(String key) {
        if (sSharedPreferences != null) {
            sSharedPreferences.edit().remove(key).commit();
        }
    }

    public static void saveInt(String key, int value) {
        if (sSharedPreferences != null) {
            sSharedPreferences.edit().putInt(key, value).commit();
        }
    }

    public static int getInt(String key) {
        int result = 0;
        if (sSharedPreferences != null) {
            result = sSharedPreferences.getInt(key, 0);
        }
        return result;
    }

    public static int getInt(String key, int defaultValue) {
        int result = defaultValue;
        if (sSharedPreferences != null) {
            result = sSharedPreferences.getInt(key, defaultValue);
        }
        return result;
    }

    public static void saveBoolean(String key, boolean value) {
        if (sSharedPreferences != null) {
            sSharedPreferences.edit().putBoolean(key, value).commit();
        }
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        boolean result = defaultValue;
        if (sSharedPreferences != null) {
            result = sSharedPreferences.getBoolean(key, defaultValue);
        }
        return result;
    }

    public static void saveFloat(String key, float value) {
        if (sSharedPreferences != null) {
            sSharedPreferences.edit().putFloat(key, value).commit();
        }
    }

    public static float getFloat(String key) {
        float result = 0f;
        if (sSharedPreferences != null) {
            result = sSharedPreferences.getFloat(key, 0f);
        }
        return result;
    }

    public static float getFloat(String key, float defaultVaule) {
        float result = defaultVaule;
        if (sSharedPreferences != null) {
            result = sSharedPreferences.getFloat(key, defaultVaule);
        }
        return result;
    }

    public static void clearAll() {
        if(sSharedPreferences != null) {
            sSharedPreferences.edit().clear().commit();

        }
    }

    public static void saveLong(String key, long value) {
        if (sSharedPreferences != null) {
            sSharedPreferences.edit().putLong(key, value).commit();
        }
    }

    public static long getLong(String key) {
        long result = 0l;
        if (sSharedPreferences != null) {
            result = sSharedPreferences.getLong(key, 0);
        }
        return result;
    }

    public static long getLong(String key, long defaultVaule) {
        long result = defaultVaule;
        if (sSharedPreferences != null) {
            result = sSharedPreferences.getLong(key, defaultVaule);
        }
        return result;
    }

    /**
     * 保存对象
     *
     * @param key     键
     * @param obj     要保存的对象（Serializable的子类）
     * @param <T>     泛型定义
     */
    public static <T extends Serializable> void putObject(String key, T obj) {
        try {
            put(key, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取对象
     *
     * @param key     键
     * @param <T>     指定泛型
     * @return 泛型对象
     */
    public static <T extends Serializable> T getObject(String key) {
        try {
            return (T) get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 存储List集合
     * @param key 存储的键
     * @param list 存储的集合
     */
    public static void putList(String key, List<? extends Serializable> list) {
        try {
            put(key, list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取List集合
     * @param key 键
     * @param <E> 指定泛型
     * @return List集合
     */
    public static <E extends Serializable> List<E> getList(String key) {
        try {
            return (List<E>) get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 存储Map集合
     * @param key 键
     * @param map 存储的集合
     * @param <K> 指定Map的键
     * @param <V> 指定Map的值
     */
    public static <K extends Serializable, V extends Serializable> void putMap(String key, Map<K, V> map)
    {
        try {
            put(key, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <K extends Serializable, V extends Serializable> Map<K, V> getMap(String key)
    {
        try {
            return (Map<K, V>) get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**存储对象*/
    private static void put(String key, Object obj)
            throws IOException
    {
        if (obj == null) {//判断对象是否为空
            return;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos  = null;
        oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        // 将对象放到OutputStream中
        // 将对象转换成byte数组，并将其进行base64编码
        String objectStr = new String(Base64.encode(baos.toByteArray()));
        baos.close();
        oos.close();

        saveString(key, objectStr);
    }

    /**获取对象*/
    private static Object get(String key)
            throws IOException, ClassNotFoundException
    {
        String wordBase64 = getString(key);
        // 将base64格式字符串还原成byte数组
        if (TextUtils.isEmpty(wordBase64)) { //不可少，否则在下面会报java.io.StreamCorruptedException
            return null;
        }
        byte[] objBytes = Base64.decode(wordBase64.getBytes());
        ByteArrayInputStream bais     = new ByteArrayInputStream(objBytes);
        ObjectInputStream ois      = new ObjectInputStream(bais);
        // 将byte数组转换成product对象
        Object obj = ois.readObject();
        bais.close();
        ois.close();
        return obj;
    }

}
