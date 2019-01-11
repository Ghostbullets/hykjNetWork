package com.hykj.network.yibook.callback;

import com.hykj.network.yibook.rec.BaseRec;

/**
 * 回调接口
 * @param <T>
 */
public interface BaseCallBack<T> {
    void onFailure(String e);

    void onResponse(Object o, T rec,BaseRec errorRec);//返回状态值为0时rec不为null，baseRec一直不为null

    void onFinish();

    T parseNetworkResponse(String result);
}
