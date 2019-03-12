package com.hykj.network.bjzhdj.http;

import android.support.annotation.Nullable;

import com.hykj.network.bjzhdj.port.AbsTransformer;
import com.hykj.network.bjzhdj.rec.BaseRec;
import com.hykj.network.bjzhdj.rec.FourResultData;
import com.hykj.network.bjzhdj.rec.MultiResultData;
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
import io.reactivex.functions.Function4;
import io.reactivex.functions.Function5;
import io.reactivex.functions.Function6;
import io.reactivex.functions.Function7;
import io.reactivex.functions.Function8;
import io.reactivex.functions.Function9;
import io.reactivex.schedulers.Schedulers;

/**
 * created by cjf
 * on:2019/2/26 14:39
 */
public class RxJavaHelper {

    /**
     * 将上游的H 转换成T，并再次发送，具体转换由继承AbsTransformer的类来处理(比如：BaseRec<T>转换成另一个xxx)
     * 可参考 handleResult()方法如何处理
     *
     * @param transformer 转换器,如果为null,则直接返回原被观察者持有的泛型
     * @param <H>         原被观察者持有的泛型
     * @param <T>         转换后返回的被观察者持有的泛型
     * @return
     */
    public static <H, T> ObservableTransformer<H, T> disposeResult(final AbsTransformer<H, T> transformer) {
        return new ObservableTransformer<H, T>() {
            @Override
            public ObservableSource<T> apply(Observable<H> upstream) {
                return upstream.flatMap(new Function<H, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(H h) throws Exception {
                        try {
                            return transformer.transformerResult(h);
                        } catch (Exception e) {
                            return (ObservableSource<T>) createData(h);
                        }
                    }
                });
            }
        };
    }

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
                                    return (ObservableSource<T>) createData(new PageData<>(result.getRows(), result.getTotal(), result.getRole()));
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
     * @param isShowProgress    是否显示弹窗
     * @param progress          进度条字符串
     * @param progressSubscribe 观察者
     * @param <T>
     * @param <H>
     * @param <Z>
     */
    public static <T, H, Z> void zipToSubscribe(Observable ob1, Observable ob2, Observable ob3, final boolean isShowProgress, final String progress, final ProgressSubscribe progressSubscribe) {
        Observable.zip(ob1.compose(handleResult()), ob2.compose(handleResult()), ob3.compose(handleResult()), new Function3<T, H, Z, ThreeResultData<T, H, Z>>() {
            @Override
            public ThreeResultData<T, H, Z> apply(T t, H h, Z z) throws Exception {//只有所有请求都成功才会走这里，并走到ProgressSubscribe的onResponse方法
                return new ThreeResultData<>(t, h, z);
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

    public static void zipToSubscribe(Observable ob1, Observable ob2, Observable ob3, final ProgressSubscribe progressSubscribe) {
        zipToSubscribe(ob1, ob2, ob3, false, null, progressSubscribe);
    }

    /**
     * 合并四个网络请求，并将他们的数据放到{@link FourResultData}类中
     *
     * @param ob1               被观察者1
     * @param ob2               被观察者2
     * @param ob3               被观察者3
     * @param ob4               被观察者4
     * @param isShowProgress    是否显示弹窗
     * @param progress          进度条字符串
     * @param progressSubscribe 观察者
     * @param <T>
     * @param <H>
     * @param <Z>
     * @param <X>
     */
    public static <T, H, Z, X> void zipToSubscribe(Observable ob1, Observable ob2, Observable ob3, Observable ob4, final boolean isShowProgress, final String progress, final ProgressSubscribe progressSubscribe) {
        Observable.zip(ob1.compose(handleResult()), ob2.compose(handleResult()), ob3.compose(handleResult()), ob4.compose(handleResult()), new Function4<T, H, Z, X, FourResultData<T, H, Z, X>>() {
            @Override
            public FourResultData<T, H, Z, X> apply(T t, H h, Z z, X x) throws Exception {
                return new FourResultData<>(t, h, z, x);
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

    public static void zipToSubscribe(Observable ob1, Observable ob2, Observable ob3, Observable ob4, final ProgressSubscribe progressSubscribe) {
        zipToSubscribe(ob1, ob2, ob3, ob4, false, null, progressSubscribe);
    }

    /**
     * 合并五个或以上的网络请求，并将他们的数据放到{@link MultiResultData}类中
     *
     * @param isShowProgress    是否显示弹窗
     * @param progress          进度条字符串
     * @param transformer       转换器，如果为null,则直接返回原被观察者持有的泛型
     * @param progressSubscribe 观察者
     * @param observables       被观察者数组
     */
    public static void multiToSubscribe(final boolean isShowProgress, final String progress, AbsTransformer transformer, final ProgressSubscribe progressSubscribe, Observable... observables) {
        if (observables == null || observables.length < 5)
            throw new RuntimeException("多网络的请求个数小于5个，请使用其他方法");
        Observable zip;
        if (observables.length == 5) {
            zip = Observable.zip(observables[0].compose(disposeResult(transformer)), observables[1].compose(disposeResult(transformer)), observables[2].compose(disposeResult(transformer)), observables[3].compose(disposeResult(transformer)), observables[4].compose(disposeResult(transformer)), new Function5<Object, Object, Object, Object, Object, MultiResultData>() {
                @Override
                public MultiResultData apply(Object o, Object o2, Object o3, Object o4, Object o5) throws Exception {
                    return new MultiResultData(o, o2, o3, o4, o5);
                }
            });
        } else if (observables.length == 6) {
            zip = Observable.zip(observables[0].compose(disposeResult(transformer)), observables[1].compose(disposeResult(transformer)), observables[2].compose(disposeResult(transformer)), observables[3].compose(disposeResult(transformer)), observables[4].compose(disposeResult(transformer)), observables[5].compose(disposeResult(transformer)), new Function6<Object, Object, Object, Object, Object, Object, MultiResultData>() {
                @Override
                public MultiResultData apply(Object o, Object o2, Object o3, Object o4, Object o5, Object o6) throws Exception {
                    return new MultiResultData(o, o2, o3, o4, o5, o6);
                }
            });
        } else if (observables.length == 7) {
            zip = Observable.zip(observables[0].compose(disposeResult(transformer)), observables[1].compose(disposeResult(transformer)), observables[2].compose(disposeResult(transformer)), observables[3].compose(disposeResult(transformer)), observables[4].compose(disposeResult(transformer)), observables[5].compose(disposeResult(transformer)), observables[6].compose(disposeResult(transformer)), new Function7<Object, Object, Object, Object, Object, Object, Object, MultiResultData>() {
                @Override
                public MultiResultData apply(Object o, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) throws Exception {
                    return new MultiResultData(o, o2, o3, o4, o5, o6, o7);
                }
            });
        } else if (observables.length == 8) {
            zip = Observable.zip(observables[0].compose(disposeResult(transformer)), observables[1].compose(disposeResult(transformer)), observables[2].compose(disposeResult(transformer)), observables[3].compose(disposeResult(transformer)), observables[4].compose(disposeResult(transformer)), observables[5].compose(disposeResult(transformer)), observables[6].compose(disposeResult(transformer)), observables[7].compose(disposeResult(transformer)), new Function8<Object, Object, Object, Object, Object, Object, Object, Object, MultiResultData>() {
                @Override
                public MultiResultData apply(Object o, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) throws Exception {
                    return new MultiResultData(o, o2, o3, o4, o5, o6, o7, o8);
                }
            });
        } else {
            zip = Observable.zip(observables[0].compose(disposeResult(transformer)), observables[1].compose(disposeResult(transformer)), observables[2].compose(disposeResult(transformer)), observables[3].compose(disposeResult(transformer)), observables[4].compose(disposeResult(transformer)), observables[5].compose(disposeResult(transformer)), observables[6].compose(disposeResult(transformer)), observables[7].compose(disposeResult(transformer)), observables[8].compose(disposeResult(transformer)), new Function9<Object, Object, Object, Object, Object, Object, Object, Object, Object, MultiResultData>() {
                @Override
                public MultiResultData apply(Object o, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) throws Exception {
                    return new MultiResultData(o, o2, o3, o4, o5, o6, o7, o8, o9);
                }
            });
        }
        zip.doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                if (isShowProgress)
                    progressSubscribe.showProgress(progress);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(progressSubscribe);
    }

    public static void multiToSubscribe(final boolean isShowProgress, final String progress, final ProgressSubscribe progressSubscribe, Observable... observables) {
        multiToSubscribe(isShowProgress, progress, null, progressSubscribe, observables);
    }

    public static void multiToSubscribe(final ProgressSubscribe progressSubscribe, Observable... observables) {
        multiToSubscribe(false, null, progressSubscribe, observables);
    }
}
