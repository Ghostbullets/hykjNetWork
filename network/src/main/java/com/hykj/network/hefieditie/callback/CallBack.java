package com.hykj.network.hefieditie.callback;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;

public abstract class CallBack<T> implements BaseCallBack<T> {
    private Class<T> cls;

    public CallBack() {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        if (type != null && type.getActualTypeArguments().length > 0) {
            cls = (Class<T>) type.getActualTypeArguments()[0];
        }
    }

    @Override
    public Object parseNetworkResponse(String result) {
        try {
            return new Gson().fromJson(result, cls);
        } catch (Exception e) {
            return result;
        }
    }
}
