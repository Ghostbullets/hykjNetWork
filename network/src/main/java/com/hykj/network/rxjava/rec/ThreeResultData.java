package com.hykj.network.rxjava.rec;

/**
 * created by cjf
 * on:2019/2/27 14:31
 * 使用Observable.zip方法后解析返回的数据
 * 同时请求3个接口返回数据
 */
public class ThreeResultData<T, H, Z> {
    private T t;
    private H h;
    private Z z;

    public ThreeResultData(T t, H h, Z z) {
        this.t = t;
        this.h = h;
        this.z = z;
    }

    public T getT() {
        return t;
    }

    public H getH() {
        return h;
    }

    public Z getZ() {
        return z;
    }
}
