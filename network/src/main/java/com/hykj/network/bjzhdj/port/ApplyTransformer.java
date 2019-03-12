package com.hykj.network.bjzhdj.port;

import io.reactivex.Observable;

/**
 * created by cjf
 * on:2019/3/12 17:46
 */
public interface ApplyTransformer<H, T> {
    Observable<T> apply(H h);
}
