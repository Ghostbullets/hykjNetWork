package com.hykj.network.zjwy.http;

import com.hykj.network.rxjava.http.AbsRxJavaHelper;
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
public class OCRRxJavaHelper<T> extends AbsRxJavaHelper<OCRRec<T>, T> {
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
    protected ObservableTransformer<OCRRec<T>, T> handleResult() {
        return new ObservableTransformer<OCRRec<T>, T>() {
            @Override
            public ObservableSource<T> apply(Observable<OCRRec<T>> upstream) {
                return upstream.flatMap(new Function<OCRRec<T>, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(OCRRec<T> bean) throws Exception {
                        if ("OK".equals(bean.getStatus())) {
                            return createData(bean.getData());
                        } else
                            return Observable.error(new ApiOCRException(bean));
                    }
                }).subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}
