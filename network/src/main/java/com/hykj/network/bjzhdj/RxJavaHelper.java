package com.hykj.network.bjzhdj;

import android.support.annotation.Nullable;

import com.hykj.network.bjzhdj.rec.BaseRec;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * created by cjf
 * on:2019/2/26 14:39
 */
public class RxJavaHelper {

    /**
     * 将上游的BaseRec<T> 转换成T，并再次发送
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<BaseRec<T>, T> handleResult() {
        return new ObservableTransformer<BaseRec<T>, T>() {
            @Override
            public ObservableSource<T> apply(Observable<BaseRec<T>> upstream) {
                return upstream.flatMap(new Function<BaseRec<T>, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(BaseRec<T> result) throws Exception {
                        if (result.getCode() == 0) {
                            if (result.getData() != null) {
                                return createData(result.getData());
                            } else {
                                return createData(result.getRows());
                            }
                        } else {
                            return Observable.error(new ApiException(result));
                        }
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 创建新的Observable并发送解析后的数据
     *
     * @param data
     * @param <T>
     * @return
     */
    private static <T> Observable<T> createData(@Nullable final T data) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                try {
                    if (data != null) {
                        emitter.onNext(data);
                    }
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }
}
