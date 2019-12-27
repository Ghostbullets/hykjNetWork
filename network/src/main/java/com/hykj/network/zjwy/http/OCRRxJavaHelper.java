package com.hykj.network.zjwy.http;


import com.base.network.rxjava.http.AbsRxJavaHelper;
import com.base.network.rxjava.http.HttpInterface;
import com.hykj.network.zjwy.rec.OCRRec;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * created by cjf
 * on:2019/5/6 10:50
 * 云脉的网络请求帮助类
 */
public class OCRRxJavaHelper<T> extends AbsRxJavaHelper {
    private static OCRRxJavaHelper mInstance;

    public static OCRRxJavaHelper getInstance() {
        if (mInstance == null) {
            synchronized (RxJavaHelper.class) {
                if (mInstance == null)
                    mInstance = new OCRRxJavaHelper();
            }
        }
        return mInstance;
    }

    @Override
    public ObservableTransformer<Object, T> handleResult() {
        return new ObservableTransformer<Object, T>() {
            @Override
            public ObservableSource<T> apply(Observable<Object> upstream) {
                return upstream.flatMap(new Function<Object, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(Object o) throws Exception {
                        //如果上游已经是T类型数据，则直接返回，不做转换
                        if (o instanceof OCRRec) {
                            OCRRec<T> bean = (OCRRec<T>) o;
                            if ("OK".equals(bean.getStatus())) {
                                return HttpInterface.createData(bean.getData());
                            } else {
                                if (isFailResultObject()) {
                                    return HttpInterface.createData(null);
                                }
                                return Observable.error(new ApiOCRException(bean));
                            }
                        } else {
                            return HttpInterface.createData((T) o);
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
