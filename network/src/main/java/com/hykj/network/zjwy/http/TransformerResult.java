package com.hykj.network.zjwy.http;

import com.hykj.network.rxjava.http.HttpInterface;
import com.hykj.network.rxjava.port.AbsTransformer;
import com.hykj.network.zjwy.rec.BaseRec;

import io.reactivex.Observable;

/**
 * created by cjf
 * on:2019/4/8 11:54
 * 该项目的具体实现类
 */
public class TransformerResult<T> implements AbsTransformer<BaseRec<T>, T> {
    @Override
    public Observable<T> transformerResult(BaseRec<T> bean) {
        if (bean.isSuccess()) {
            return HttpInterface.createData(bean.getData());
        } else {
            return Observable.error(new ApiException(bean));
        }
    }
}
