package com.hykj.network.zjwy.http;


import com.base.network.rxjava.http.HttpInterface;
import com.base.network.rxjava.port.AbsTransformer;
import com.hykj.network.zjwy.rec.BaseRec;

import io.reactivex.Observable;

/**
 * created by cjf
 * on:2019/4/8 11:54
 * 该项目的具体实现类
 */
public class TransformerResult<T> extends AbsTransformer<BaseRec<T>, T> {
    public TransformerResult() {
    }

    public TransformerResult(boolean isFailResultObject) {
        super(isFailResultObject);
    }


    @Override
    public Observable<T> transformerResult(BaseRec<T> bean) {
        if (bean.isSuccess()) {
            return HttpInterface.createData(bean.getData());
        } else {
            if (isFailResultObject()) {
                return HttpInterface.createData(null);
            }
            return Observable.error(new ApiException(bean));
        }
    }
}
