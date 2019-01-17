package com.hykj.network.xxgj.callback;


import com.google.gson.Gson;
import com.hykj.network.xxgj.rec.BaseRec;

import java.lang.reflect.ParameterizedType;

public abstract class ObtainCallBack<T extends BaseRec> implements BaseCallBack<T> {
    private Class<T> t;

    public ObtainCallBack() {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        if (type != null && type.getActualTypeArguments().length > 0) {
            t = (Class<T>) type.getActualTypeArguments()[0];
        }
    }

    public ObtainCallBack(Class<T> t) {
        this.t = t;
    }

    @Override
    public void onFinish() {

    }

    @Override
    public T parseNetworkResponse(String result) {
        try {
            return new Gson().fromJson(result, t);
        } catch (Exception e) {
            return null;
        }
    }
}
