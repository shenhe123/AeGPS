package com.linkface.baselibrary.utils;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Description:
 * Author: shenhe
 * Date: 2018/12/6.
 */

public class JsonUtil {
    /**
     * 将javaBean转换成JSON对象
     *
     * @param paramObject
     *            需要解析的对象
     */
    public static String javaBeanToJson(Object paramObject) {
        String json = JSON.toJSONString(paramObject);
        return json;
    }

    /**
     * 对单个javaBean进行解析
     *
     * @param <T>
     * @param paramJson 需要解析的JSON字符串
     * @param paramCls 需要转换成的类
     * @return
     */
    public static <T> T jsonToJavaBean(String paramJson, Class<T> paramCls) {
        T t = null;
        try {
            t = JSON.parseObject(paramJson, paramCls);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 将JSON转换正JavaBeanList
     * @param paramJson
     * @param paramCls
     * @return
     */
    public static <T> List<T> jsonToJavaBeanList(String paramJson,
                                                 Class<T> paramCls) {
        List<T> ts = null;
        try {
            ts = JSON.parseArray(paramJson, paramCls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ts;
    }
}
