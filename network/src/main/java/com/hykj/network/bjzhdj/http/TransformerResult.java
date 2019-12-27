package com.hykj.network.bjzhdj.http;

import com.base.network.rxjava.http.HttpInterface;
import com.base.network.rxjava.port.AbsTransformer;
import com.hykj.network.bjzhdj.rec.BaseRec;
import com.hykj.network.bjzhdj.rec.PageData;

import io.reactivex.Observable;

public class TransformerResult<T> extends AbsTransformer<BaseRec<T>, T> {
    @Override
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
    }
}
