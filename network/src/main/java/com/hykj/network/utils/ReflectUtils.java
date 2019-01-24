package com.hykj.network.utils;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReflectUtils {

    /**
     * 通过反射将类中的属性名、属性值以键值对返回
     *
     * @param params    传入的map集合
     * @param targetCls 目标class
     * @param floorCls  底层class，目标class持续的从父类中反射属性键值对，直到它的父类名等于该类名
     * @return
     */
    public static Map<String, String> progressData(Map<String, String> params, @NonNull Class<?> targetCls, Class<?> floorCls) {
        if (params == null)
            params = new LinkedHashMap<>();
        if (floorCls == null)
            floorCls = Object.class;
        Gson gson = new Gson();
        while (!targetCls.getName().equals(floorCls.getName())) {
            try {
                Field[] fields = targetCls.getDeclaredFields();
                for (Field field : fields) {
                    String key = field.getName();
                    if ("serialVersionUID".equals(key))
                        continue;
                    field.setAccessible(true);
                    Object o = field.get(targetCls);
                    if (o == null)
                        continue;
                    String value;
                    if (o instanceof String)
                        value = (String) o;
                    else
                        value = gson.toJson(o);
                    params.put(key, value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            targetCls = targetCls.getSuperclass();
        }
        return params;
    }
}
