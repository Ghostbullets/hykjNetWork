package com.hykj.network.yibook.callback;


import com.google.gson.Gson;
import com.hykj.hykjnetwork.yibook.rec.BaseRec;

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
