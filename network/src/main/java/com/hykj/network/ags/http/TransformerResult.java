package com.hykj.network.ags.http;

import com.base.network.rxjava.http.HttpInterface;
import com.base.network.rxjava.port.AbsTransformer;
import com.hykj.network.ags.rec.BaseRec;
import com.hykj.network.ags.rec.PageData;


import io.reactivex.Observable;

/**
 * created by cjf
 * on:2019/4/11 19:35
 */
public class TransformerResult<T> extends AbsTransformer<BaseRec<T>, T> {
    public TransformerResult() {
    }

    public TransformerResult(boolean isFailResultObject) {
        super(isFailResultObject);
    }

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
            if (isFailResultObject) {
                return HttpInterface.createData(null);
            }
            return Observable.error(new ApiException(bean));
        }
    }
}
