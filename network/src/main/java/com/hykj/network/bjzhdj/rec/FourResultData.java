package com.hykj.network.bjzhdj.rec;

/**
 * created by cjf
 * on:2019/2/27 14:31
 * 使用Observable.zip方法后解析返回的数据
 * 同时请求4个接口返回数据
 */
public class FourResultData<T, H, Z, X> {
    private T t;
    private H h;
    private Z z;
    private X x;

    public FourResultData(T t, H h, Z z, X x) {
        this.t = t;
        this.h = h;
        this.z = z;
        this.x = x;
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

    public X getX() {
        return x;
    }
}
