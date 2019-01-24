package com.hykj.network.hefieditie.callback;

public interface BaseCallBack<T> {
    void onFailure(String e);

    void onResponse(Object o, T rec);

    Object parseNetworkResponse(String result);
}
