package com.hykj.network.rxjava.port;


import com.hykj.network.rxjava.http.EasyHttp;

import java.util.concurrent.TimeUnit;

/**
 * created by cjf
 * on:2019/4/12 17:38
 * 轮询回调 {@link EasyHttp.Builder#interval(long, long, TimeUnit, int, IntervalCallBack)} (long, long, TimeUnit, int)}
 */
public interface IntervalCallBack<T> {

    /**
     * 判断t是否满足条件，如果满足返回true代表终止轮询，false则继续轮询
     *
     * @param t 网络请求解析后的数据，比如Observable<BaseRec<T>> 的话，这个t则是BaseRec<T>或者T
     * @return
     */
    boolean takeUntil(T t);
}
