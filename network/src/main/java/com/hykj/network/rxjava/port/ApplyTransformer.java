package com.hykj.network.rxjava.port;

import io.reactivex.Observable;

/**
 * created by cjf
 * on:2019/3/12 17:46
 * 用于嵌套请求，将上一个请求获取到的数据进行处理，并发送一个新的网络请求被观察者
 */
public interface ApplyTransformer<H, T> {
    Observable<T> apply(H h);
}
