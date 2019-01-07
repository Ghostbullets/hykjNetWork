package com.hykj.network.xxgj.callback;


import com.google.gson.Gson;
import com.hykj.hykjnetwork.xxgj.rec.BaseRec;

public abstract class ObtainCallBack<T extends BaseRec> implements BaseCallBack<T> {
    private Class<T> t;

    public ObtainCallBack(Class<T> t) {
        this.t = t;
    }

    @Override
    public void onFinish() {

    }

    @Override
    public T parseNetworkResponse(String result) {
        return new Gson().fromJson(result, t);
    }
}