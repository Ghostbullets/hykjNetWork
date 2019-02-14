package com.hykj.network.yibook.get;

public interface BaseGetCallBack<T> {
    void onFailure(String e);

    void onResponse(Object o, T rec);

    void onFinish();

    T parseNetworkResponse(String result);
}
