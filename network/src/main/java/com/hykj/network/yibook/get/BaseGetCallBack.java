package com.hykj.network.yibook.get;

public interface BaseGetCallBack<T> {
    void onFailure(String e);

    void onResponse(Object o, T rec,BaseGetRec errorRec);//返回状态值为0时rec不为null，baseRec一直不为null

    void onFinish();

    T parseNetworkResponse(String result);
}
