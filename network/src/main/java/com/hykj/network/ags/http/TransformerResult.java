package com.hykj.network.ags.http;

import com.hykj.network.ags.rec.BaseRec;
import com.hykj.network.ags.rec.PageData;
import com.hykj.network.rxjava.http.HttpInterface;
import com.hykj.network.rxjava.port.AbsTransformer;

import io.reactivex.Observable;

/**
 * created by cjf
 * on:2019/4/11 19:35
 */
public class TransformerResult<T> implements AbsTransformer<BaseRec<T>,T> {
    @Override
    public Observable<T> transformerResult(BaseRec<T> bean) {
        if (bean.getCode() == 0) {
            if (bean.getData() != null) {
                return HttpInterface.createData(bean.getData());
            } else {
                try {
                    return HttpInterface.createData((T) new PageData<>(bean.getRows(), bean.getTotal()));
                } catch (Exception e) {
                    return HttpInterface.createData((T) bean.getRows());
                }
            }
        } else {
            return Observable.error(new ApiException(bean));
        }
    }
}