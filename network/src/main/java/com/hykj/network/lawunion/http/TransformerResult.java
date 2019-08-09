package com.hykj.network.lawunion.http;

import com.base.network.rxjava.http.HttpInterface;
import com.base.network.rxjava.port.AbsTransformer;
import com.base.network.rxjava.rec.PageData;
import com.hykj.network.lawunion.rec.BaseRec;

import io.reactivex.Observable;

/**
 * created by cjf
 * on: 2019/8/9
 */
public class TransformerResult<T> extends AbsTransformer<BaseRec<T>, T> {
    public TransformerResult() {
    }

    public TransformerResult(boolean isFailResultObject) {
        super(isFailResultObject);
    }

    @Override
    public Observable<T> transformerResult(BaseRec<T> bean) {
        if (bean.getCode() != null && bean.getCode() == 0) {
            if (bean.getData() != null) {//普通数据返回
                return HttpInterface.createData(bean.getData());
            } else {
                try {//列表数据返回PageData对象，包含列表数据rows跟总数量total两个参数，前提是在ProgressSubscribe传入PageData泛型
                    return HttpInterface.createData((T) new PageData<>(bean.getRows(), bean.getTotal()));
                } catch (Exception e) {
                    return HttpInterface.createData((T) bean.getRows());//ProgressSubscribe传入List<XXX对象>泛型时返回
                }
            }
        } else {
            if (isFailResultObject) {
                //当接口报错时，如果希望返回一个空的被观察者时可设置该变量为true
                // (PS:常用于zip合并接口请求时使用，防止其中一个接口报错导致其他接口无法获取数据)
                return HttpInterface.createData(null);
            }
            return Observable.error(new ApiException(bean));
        }
    }
}
