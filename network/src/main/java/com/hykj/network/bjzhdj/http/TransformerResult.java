package com.hykj.network.bjzhdj.http;

import com.base.network.rxjava.http.HttpInterface;
import com.base.network.rxjava.port.AbsTransformer;
import com.hykj.network.bjzhdj.rec.BaseRec;
import com.hykj.network.bjzhdj.rec.PageData;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Observable;

public class TransformerResult extends AbsTransformer {
    /*@Override
    public Observable<T> transformerResult(BaseRec<T> bean) {
        if (bean.getCode() != null && bean.getCode() == 0) {
            if (bean.getData() != null) {
                return HttpInterface.createData(bean.getData());
            } else {
                try {
                    return HttpInterface.createData((T) new PageData<>(bean.getRows(), bean.getTotal(), bean.getRole()));
                } catch (Exception e) {
                    return HttpInterface.createData(bean.getRows());
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
        if (h instanceof BaseRec){
            BaseRec<T> bean= (BaseRec<T>) h;
            if (bean.getCode() != null && bean.getCode() == 0) {
                if (bean.getData() != null) {
                    return HttpInterface.createData(bean.getData());
                } else {
                    try {
                        return HttpInterface.createData((T) new PageData<>(bean.getRows(), bean.getTotal(), bean.getRole()));
                    } catch (Exception e) {
                        return HttpInterface.createData(bean.getRows());
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
