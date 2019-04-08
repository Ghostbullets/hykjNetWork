package com.hykj.network.rxjava.rec;

/**
 * created by cjf
 * on:2019/2/28 16:00
 * 使用Observable.zip方法后解析返回的数据
 * 同时请求5-9个接口返回数据
 */
public class MultiResultData {
    private Object[] objects;

    public MultiResultData(Object... objects) {
        this.objects = objects;
    }

    public Object[] getObjects() {
        return objects;
    }
}
