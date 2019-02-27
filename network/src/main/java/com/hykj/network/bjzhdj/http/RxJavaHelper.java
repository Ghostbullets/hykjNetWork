package com.hykj.network.bjzhdj.http;

import android.support.annotation.Nullable;

import com.hykj.network.bjzhdj.rec.BaseRec;
import com.hykj.network.bjzhdj.rec.PageData;
import com.hykj.network.bjzhdj.rec.ResultData;
import com.hykj.network.bjzhdj.rec.ThreeResultData;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;

/**
 * created by cjf
 * on:2019/2/26 14:39
 * 后续改进可以从复用性方面考虑，比如 BaseRec<T> 是否可以改成 T  ApiException里面的参数是否也可以因此改成T
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
                                try {
                                    return (ObservableSource<T>) createData(new PageData<>(result.getRows(), result.getTotal()));
                                } catch (Exception e) {
                                    return createData(result.getRows());
                                }
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

    /**
     * 添加线程管理并订阅
     * activity中使用
     *
     * @param ob                被观察者
     * @param isShowProgress    是否显示弹窗
     * @param progress          进度条字符串
     * @param progressSubscribe 观察者
     */
    public static void toSubscribe(Observable ob, final boolean isShowProgress, final String progress, final ProgressSubscribe progressSubscribe) {
        ob.compose(handleResult()).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                if (isShowProgress) {
                    progressSubscribe.showProgress(progress);
                }
            }
        }).subscribe(progressSubscribe);
    }

    public static void toSubscribe(Observable ob, final boolean isShowProgress, final ProgressSubscribe progressSubscribe) {
        toSubscribe(ob, isShowProgress, null, progressSubscribe);
    }

    public static void toSubscribe(Observable ob, final ProgressSubscribe progressSubscribe) {
        toSubscribe(ob, false, null, progressSubscribe);
    }

    /**
     * 合并两个网络请求，并将他们的数据放到{@link ResultData}类中
     *
     * @param ob1               第一个网络请求
     * @param ob2               第二个网络请求
     * @param isShowProgress    是否显示弹窗
     * @param progress          进度条字符串
     * @param progressSubscribe 观察者
     * @param <T>
     * @param <H>
     */
    public static <T, H> void zipToSubscribe(Observable ob1, Observable ob2, final boolean isShowProgress, final String progress, final ProgressSubscribe progressSubscribe) {
        Observable.zip(ob1.compose(handleResult()), ob2.compose(handleResult()), new BiFunction<T, H, ResultData<T, H>>() {
            @Override
            public ResultData<T, H> apply(T t, H h) throws Exception {//只有所有请求都成功才会走这里，并走到ProgressSubscribe的onResponse方法
                return new ResultData<>(t, h);
            }
        }).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                if (isShowProgress) {
                    progressSubscribe.showProgress(progress);
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(progressSubscribe);
    }

    public static void zipToSubscribe(Observable ob1, Observable ob2, final boolean isShowProgress, final ProgressSubscribe progressSubscribe) {
        zipToSubscribe(ob1, ob2, isShowProgress, null, progressSubscribe);
    }

    public static void zipToSubscribe(Observable ob1, Observable ob2, final ProgressSubscribe progressSubscribe) {
        zipToSubscribe(ob1, ob2, false, null, progressSubscribe);
    }

    /**
     * 合并三个网络请求，并将他们的数据放到{@link ThreeResultData}类中
     *
     * @param ob1               被观察者1
     * @param ob2               被观察者2
     * @param ob3               被观察者3
     * @param progress          进度条字符串
     * @param progressSubscribe 观察者
     * @param <T>
     * @param <H>
     * @param <Z>
     */
    public static <T, H, Z> void zipToSubscribe(Observable ob1, Observable ob2, Observable ob3, final String progress, final ProgressSubscribe progressSubscribe) {
        Observable.zip(ob1.compose(handleResult()), ob2.compose(handleResult()), ob3.compose(handleResult()), new Function3<T, H, Z, ThreeResultData<T, H, Z>>() {
            @Override
            public ThreeResultData<T, H, Z> apply(T t, H h, Z z) throws Exception {//只有所有请求都成功才会走这里，并走到ProgressSubscribe的onResponse方法
                return new ThreeResultData<>(t, h, z);
            }
        }).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                progressSubscribe.showProgress(progress);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(progressSubscribe);
    }
}
