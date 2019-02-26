package com.hykj.network.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReflectUtils {

    /**
     * 通过反射将类中的属性名、属性值以键值对返回
     *
     * @param params    传入的map集合
     * @param target 目标类
     * @param floorCls  底层class，目标class持续的从父类中反射属性键值对，直到它的父类名等于该类名
     * @return
     */
    public static Map<String, String> progressData(Map<String, String> params, @NonNull Object target, Class<?> floorCls) {
        Class<?> targetCls=target.getClass();
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
                    Object o = field.get(target);
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

    /**
     * 反射修改对象属性值
     * @param obj 类对象
     * @param fieldName 类属性名
     * @param value 类属性名对应的属性值
     */
    public static void setFieldValue(Object obj, String fieldName, Object value) {
        if (obj == null || TextUtils.isEmpty(fieldName)) return;

        Class<?> clazz = obj.getClass();
        while (clazz != Object.class) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(fieldName, value);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
            clazz = clazz.getSuperclass();
        }
    }
}
