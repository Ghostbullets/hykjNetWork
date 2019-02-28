package com.hykj.network.bjzhdj.rec;

/**
 * created by cjf
 * on:2019/2/27 14:06
 * 使用Observable.zip方法后解析返回的数据
 * 同时请求2个接口返回数据
 */
public class ResultData<T,H> {
    private T t;
    private H h;

    public ResultData(T t, H h) {
        this.t = t;
        this.h = h;
    }

    public T getT() {
        return t;
    }

    public H getH() {
        return h;
    }
}
