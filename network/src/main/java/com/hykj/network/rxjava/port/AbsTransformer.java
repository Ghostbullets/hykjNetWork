package com.hykj.network.rxjava.port;

import io.reactivex.Observable;

/**
 * created by cjf
 * on:2019/2/28 16:19
 * 由使用者继承该抽象类来将原被观察者的泛型转为新的被观察者
 */
public abstract class AbsTransformer<H, T> {
    //举例:一个页面可以选择上传或者不上传图片、视频，这时候如果使用合并网络请求，要进行2次网络请求，如果实际上只上传了图片，而没有视频的话，其中一个网络请求可能会报错
    //这时候程序走失败回调，所以加入下面这个参数，判断是否在请求失败时走成功回调。
    protected boolean isFailResultObject;//是否在数据获取失败时返回Object泛型的被观察者，而不是error的被观察者,默认false

    public AbsTransformer() {
    }

    public AbsTransformer(boolean isFailResultObject) {
        this.isFailResultObject = isFailResultObject;
    }

    public abstract Observable<T> transformerResult(H h);
}
