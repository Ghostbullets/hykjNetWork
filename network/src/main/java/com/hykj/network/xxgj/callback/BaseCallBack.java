package com.hykj.network.xxgj.callback;

/**
 * 回调接口
 * @param <T>
 */
public interface BaseCallBack<T> {
    void onFailure(String e);

    void onResponse(Object o, T rec);

    void onFinish();

    T parseNetworkResponse(String result);
}
