package com.hykj.network.ags.http;

import com.base.network.rxjava.http.HttpInterface;
import com.base.network.rxjava.port.AbsTransformer;
import com.base.network.rxjava.rec.PageData;
import com.hykj.network.ags.rec.BaseRec;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Observable;

/**
 * created by cjf
 * on:2019/4/11 19:35
 */
public class TransformerResult extends AbsTransformer {
    public TransformerResult() {
    }

    public TransformerResult(boolean isFailResultObject) {
        super(isFailResultObject);
    }

    /*@Override
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
            if (isFailResultObject()) {
                return HttpInterface.createData(null);
            }
            return Observable.error(new ApiException(bean));
        }
    }*/

    @NotNull
    @Override
    public <H, T> Observable<T> transformerResult(H h) {
        if (h instanceof BaseRec) {
            BaseRec<T> bean = (BaseRec<T>) h;
            if (bean.getCode() != null &&bean.getCode() == 0) {
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
                if (isFailResultObject()) {
                    return HttpInterface.createData(null);
                }
                return Observable.error(new ApiException(bean));
            }
        }
        return (Observable<T>) HttpInterface.createData(h);
    }
}
