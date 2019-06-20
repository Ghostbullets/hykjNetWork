package com.hykj.network.ags.http;

import com.hykj.network.ags.rec.BaseRec;
import com.hykj.network.ags.rec.PageData;
import com.hykj.network.rxjava.http.AbsRxJavaHelper;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * created by cjf
 * on:2019/4/11 19:21
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
                            if (bean.getCode() == 0) {
                                if (bean.getData() != null) {
                                    return createData(bean.getData());
                                } else {
                                    try {
                                        return createData((T) new PageData<>(bean.getRows(), bean.getTotal()));
                                    } catch (Exception e) {
                                        return createData((T) bean.getRows());
                                    }
                                }
                            } else {
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
