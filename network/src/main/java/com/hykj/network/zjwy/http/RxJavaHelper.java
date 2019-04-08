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
public class RxJavaHelper<T> extends AbsRxJavaHelper<BaseRec<T>, T> {
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
    protected ObservableTransformer<BaseRec<T>, T> handleResult() {
        return new ObservableTransformer<BaseRec<T>, T>() {
            @Override
            public ObservableSource<T> apply(Observable<BaseRec<T>> upstream) {
                return upstream.flatMap(new Function<BaseRec<T>, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(BaseRec<T> bean) throws Exception {
                        if ("true".equals(bean.getSuccess())) {
                            return createData(bean.getData());
                        } else {
                            return Observable.error(new ApiException(bean));
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
