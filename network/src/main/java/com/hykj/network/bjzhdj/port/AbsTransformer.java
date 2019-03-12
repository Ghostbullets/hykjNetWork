package com.hykj.network.bjzhdj.port;

import io.reactivex.Observable;

/**
 * created by cjf
 * on:2019/2/28 16:19
 * 由使用者继承该抽象类来将原被观察者的泛型转为新的被观察者
 */
public interface AbsTransformer<H, T> {
    Observable<T> transformerResult(H h);
}
