package com.hykj.network.bjzhdj.callback;

/**
 * 回调接口
 * @param <T>
 */
public interface BaseCallBack<T> {
    void onFailure(Throwable e);

    void onResponse(T rec);

    void onFinish();

    Object parseNetworkResponse(String result);
}
