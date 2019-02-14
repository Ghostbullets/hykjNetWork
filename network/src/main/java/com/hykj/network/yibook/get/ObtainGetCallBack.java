package com.hykj.network.yibook.get;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;

public abstract class ObtainGetCallBack<T extends BaseGetRec> implements BaseGetCallBack {
    private Class<T> cls;

    public ObtainGetCallBack() {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        if (type != null && type.getActualTypeArguments().length > 0) {
            cls = (Class<T>) type.getActualTypeArguments()[0];
        }
    }

    @Override
    public void onFinish() {

    }

    @Override
    public Object parseNetworkResponse(String result) {
        try {
            return new Gson().fromJson(result, cls);
        } catch (Exception e) {
            return null;
        }
    }
}
