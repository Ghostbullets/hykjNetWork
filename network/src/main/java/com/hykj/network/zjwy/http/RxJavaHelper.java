package com.hykj.network.zjwy.http;

import com.hykj.network.rxjava.http.AbsRxJavaHelper;
import com.hykj.network.zjwy.rec.BaseRec;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * created by cjf
 * on:2019/4/8 11:15
 * 该项目实现网络请求数据转换
 */
public class RxJavaHelper<T> extends AbsRxJavaHelper<T> {
    private static RxJavaHelper mInstance;

    public static RxJavaHelper getInstance() {
        if (mInstance == null) {
            synchronized (RxJavaHelper.class) {
                if (mInstance == null)
                    mInstance = new RxJavaHelper();
            }
        }
        return mInstance;
    }


    @Override
    protected ObservableTransformer<Object, T> handleResult() {
        return new ObservableTransformer<Object, T>() {
            @Override
            public ObservableSource<T> apply(Observable<Object> upstream) {
                return upstream.flatMap(new Function<Object, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(Object o) throws Exception {
                        //如果上游已经是T类型数据，则直接返回，不做转换
                        if (o instanceof BaseRec) {
                            BaseRec<T> bean = (BaseRec<T>) o;
                            if ("true".equals(bean.getSuccess())) {
                                return createData(bean.getData());
                            } else {
                                if (isFailResultObject) {
                                    return createData(null);
                                }
                                return Observable.error(new ApiException(bean));
                            }
                        } else {
                            return createData((T) o);
                        }
                    }
                }).subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}
