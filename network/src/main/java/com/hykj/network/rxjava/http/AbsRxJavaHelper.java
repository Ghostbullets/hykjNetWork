package com.hykj.network.rxjava.http;

import android.support.annotation.Nullable;

import com.hykj.network.rxjava.port.RxView;
import com.hykj.network.rxjava.rec.FourResultData;
import com.hykj.network.rxjava.rec.MultiResultData;
import com.hykj.network.rxjava.rec.ResultData;
import com.hykj.network.rxjava.rec.ThreeResultData;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;
import io.reactivex.functions.Function4;
import io.reactivex.functions.Function5;
import io.reactivex.functions.Function6;
import io.reactivex.functions.Function7;
import io.reactivex.functions.Function8;
import io.reactivex.functions.Function9;

/**
 * created by cjf
 * on:2019/2/26 14:39
 */
public abstract class AbsRxJavaHelper<T> {

    protected boolean isFailResultObject;//是否在数据获取失败时返回Object泛型的被观察者，而不是error的被观察者,默认false，请注意在网络请求结束后恢复状态为false

    public void setFailResultObject(boolean isFailResultObject) {
        this.isFailResultObject = isFailResultObject;
    }

    //处理网络请求返回的数据，配合isFailResultObject参数使用
    public Object disposeResult(Object o) {
        if (o.getClass().getName().equals(Object.class.getName())) {
            return null;
        }
        return o;
    }

    /**
     * 将上游的H 转换成T，并再次发送，具体转换由继承AbsRxJavaHelper的类来处理(比如：BaseRec<T>转换成T)
     * 可参考 bjzhdj里面的RxJavaHelper 的handleResult()方法如何处理{@link com.hykj.network.bjzhdj.http.RxJavaHelper {@link #handleResult()}}
     *
     * @return
     */
    protected abstract ObservableTransformer<Object, T> handleResult();

    /**
     * 创建新的Observable并发送解析后的数据
     *
     * @param data
     * @return
     */
    public Observable<T> createData(@Nullable final T data) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                try {
                    if (data != null) {
                        emitter.onNext(data);
                    } else {
                        emitter.onNext((T) new Object());
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
     * @param mView             接口
     * @param event             在Activity页面调用时请传入{@link ActivityEvent}，在Fragment碎片调用时请传入{@link FragmentEvent}
     * @param progressSubscribe 观察者
     */
    public void toSubscribe(Observable ob, final boolean isShowProgress, final String progress, RxView mView, Object event, final ProgressSubscribe progressSubscribe) {
        if (mView != null && mView.bindToUntilEvent(event) != null) {
            ob = ob.compose(mView.bindToUntilEvent(event));
        }
        ob.compose(handleResult()).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                if (isShowProgress) {
                    progressSubscribe.showProgress(progress);
                }
                progressSubscribe.preLoad();
            }
        }).subscribe(progressSubscribe);
    }

    public void toSubscribe(Observable ob, final boolean isShowProgress, RxView mView, Object event, final ProgressSubscribe progressSubscribe) {
        toSubscribe(ob, isShowProgress, null, mView, event, progressSubscribe);
    }

    public void toSubscribe(Observable ob, RxView mView, Object event, final ProgressSubscribe progressSubscribe) {
        toSubscribe(ob, false, mView, event, progressSubscribe);
    }

    /**
     * 合并两个网络请求，并将他们的数据放到{@link ResultData}类中
     *
     * @param ob1               第一个网络请求
     * @param ob2               第二个网络请求
     * @param isShowProgress    是否显示弹窗
     * @param progress          进度条字符串
     * @param mView             接口
     * @param event             在Activity页面调用时请传入{@link ActivityEvent}，在Fragment碎片调用时请传入{@link FragmentEvent}
     * @param progressSubscribe 观察者
     * @param <T>
     * @param <H>
     */
    public <T, H> void zipToSubscribe(Observable ob1, Observable ob2, final boolean isShowProgress, final String progress, RxView mView, Object event, final ProgressSubscribe progressSubscribe) {
        Observable zip = Observable.zip(ob1.compose(handleResult()), ob2.compose(handleResult()), new BiFunction<Object, Object, ResultData<T, H>>() {
            @Override
            public ResultData<T, H> apply(Object t, Object h) throws Exception {//只有所有请求都成功才会走这里，并走到ProgressSubscribe的onResponse方法
                return (ResultData<T, H>) new ResultData<>(disposeResult(t), disposeResult(h));
            }
        });
        if (mView != null && mView.bindToUntilEvent(event) != null) {
            zip = zip.compose(mView.bindToUntilEvent(event));
        }
        zip.doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                if (isShowProgress) {
                    progressSubscribe.showProgress(progress);
                }
                progressSubscribe.preLoad();
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(progressSubscribe);
    }

    public void zipToSubscribe(Observable ob1, Observable ob2, final boolean isShowProgress, RxView mView, Object event, final ProgressSubscribe progressSubscribe) {
        zipToSubscribe(ob1, ob2, isShowProgress, null, mView, event, progressSubscribe);
    }

    public void zipToSubscribe(Observable ob1, Observable ob2, RxView mView, Object event, final ProgressSubscribe progressSubscribe) {
        zipToSubscribe(ob1, ob2, false, mView, event, progressSubscribe);
    }

    /**
     * 合并三个网络请求，并将他们的数据放到{@link ThreeResultData}类中
     *
     * @param ob1               被观察者1
     * @param ob2               被观察者2
     * @param ob3               被观察者3
     * @param isShowProgress    是否显示弹窗
     * @param progress          进度条字符串
     * @param mView             接口
     * @param event             在Activity页面调用时请传入{@link ActivityEvent}，在Fragment碎片调用时请传入{@link FragmentEvent}
     * @param progressSubscribe 观察者
     * @param <T>
     * @param <H>
     * @param <Z>
     */
    public <T, H, Z> void zipToSubscribe(Observable ob1, Observable ob2, Observable ob3, final boolean isShowProgress, final String progress, RxView mView, Object event, final ProgressSubscribe progressSubscribe) {
        Observable zip = Observable.zip(ob1.compose(handleResult()), ob2.compose(handleResult()), ob3.compose(handleResult()), new Function3<Object, Object, Object, ThreeResultData<T, H, Z>>() {
            @Override
            public ThreeResultData<T, H, Z> apply(Object t, Object h, Object z) throws Exception {//只有所有请求都成功才会走这里，并走到ProgressSubscribe的onResponse方法
                return (ThreeResultData<T, H, Z>) new ThreeResultData<>(disposeResult(t), disposeResult(h), disposeResult(z));
            }
        });
        if (mView != null && mView.bindToUntilEvent(event) != null) {
            zip = zip.compose(mView.bindToUntilEvent(event));
        }
        zip.doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                if (isShowProgress) {
                    progressSubscribe.showProgress(progress);
                }
                progressSubscribe.preLoad();
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(progressSubscribe);
    }

    public void zipToSubscribe(Observable ob1, Observable ob2, Observable ob3, RxView mView, Object event, final ProgressSubscribe progressSubscribe) {
        zipToSubscribe(ob1, ob2, ob3, false, null, mView, event, progressSubscribe);
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
     * @param mView             接口
     * @param event             在Activity页面调用时请传入{@link ActivityEvent}，在Fragment碎片调用时请传入{@link FragmentEvent}
     * @param progressSubscribe 观察者
     * @param <T>
     * @param <H>
     * @param <Z>
     * @param <X>
     */
    public <T, H, Z, X> void zipToSubscribe(Observable ob1, Observable ob2, Observable ob3, Observable ob4, final boolean isShowProgress, final String progress, RxView mView, Object event, final ProgressSubscribe progressSubscribe) {
        Observable zip = Observable.zip(ob1.compose(handleResult()), ob2.compose(handleResult()), ob3.compose(handleResult()), ob4.compose(handleResult()), new Function4<Object, Object, Object, Object, FourResultData<T, H, Z, X>>() {
            @Override
            public FourResultData<T, H, Z, X> apply(Object t, Object h, Object z, Object x) throws Exception {
                return (FourResultData<T, H, Z, X>) new FourResultData<>(disposeResult(t), disposeResult(h), disposeResult(z), disposeResult(x));
            }
        });
        if (mView != null && mView.bindToUntilEvent(event) != null) {
            zip = zip.compose(mView.bindToUntilEvent(event));
        }
        zip.doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                if (isShowProgress) {
                    progressSubscribe.showProgress(progress);
                }
                progressSubscribe.preLoad();
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(progressSubscribe);
    }

    public void zipToSubscribe(Observable ob1, Observable ob2, Observable ob3, Observable ob4, RxView mView, Object event, final ProgressSubscribe progressSubscribe) {
        zipToSubscribe(ob1, ob2, ob3, ob4, false, null, mView, event, progressSubscribe);
    }

    /**
     * 合并五个或以上的网络请求，并将他们的数据放到{@link MultiResultData}类中
     *
     * @param isShowProgress    是否显示弹窗
     * @param progress          进度条字符串
     * @param mView             接口
     * @param event             在Activity页面调用时请传入{@link ActivityEvent}，在Fragment碎片调用时请传入{@link FragmentEvent}
     * @param progressSubscribe 观察者
     * @param observables       被观察者数组
     */
    public void multiToSubscribe(final boolean isShowProgress, final String progress, RxView mView, Object event, final ProgressSubscribe progressSubscribe, Observable... observables) {
        if (observables == null || observables.length < 5)
            throw new RuntimeException("多网络的请求个数小于5个，请使用其他方法");
        Observable zip;
        if (observables.length == 5) {
            zip = Observable.zip(observables[0].compose(handleResult()), observables[1].compose(handleResult()), observables[2].compose(handleResult()), observables[3].compose(handleResult()), observables[4].compose(handleResult()), new Function5<Object, Object, Object, Object, Object, MultiResultData>() {
                @Override
                public MultiResultData apply(Object o, Object o2, Object o3, Object o4, Object o5) throws Exception {
                    return new MultiResultData(o, o2, o3, o4, o5);
                }
            });
        } else if (observables.length == 6) {
            zip = Observable.zip(observables[0].compose(handleResult()), observables[1].compose(handleResult()), observables[2].compose(handleResult()), observables[3].compose(handleResult()), observables[4].compose(handleResult()), observables[5].compose(handleResult()), new Function6<Object, Object, Object, Object, Object, Object, MultiResultData>() {
                @Override
                public MultiResultData apply(Object o, Object o2, Object o3, Object o4, Object o5, Object o6) throws Exception {
                    return new MultiResultData(o, o2, o3, o4, o5, o6);
                }
            });
        } else if (observables.length == 7) {
            zip = Observable.zip(observables[0].compose(handleResult()), observables[1].compose(handleResult()), observables[2].compose(handleResult()), observables[3].compose(handleResult()), observables[4].compose(handleResult()), observables[5].compose(handleResult()), observables[6].compose(handleResult()), new Function7<Object, Object, Object, Object, Object, Object, Object, MultiResultData>() {
                @Override
                public MultiResultData apply(Object o, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) throws Exception {
                    return new MultiResultData(o, o2, o3, o4, o5, o6, o7);
                }
            });
        } else if (observables.length == 8) {
            zip = Observable.zip(observables[0].compose(handleResult()), observables[1].compose(handleResult()), observables[2].compose(handleResult()), observables[3].compose(handleResult()), observables[4].compose(handleResult()), observables[5].compose(handleResult()), observables[6].compose(handleResult()), observables[7].compose(handleResult()), new Function8<Object, Object, Object, Object, Object, Object, Object, Object, MultiResultData>() {
                @Override
                public MultiResultData apply(Object o, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) throws Exception {
                    return new MultiResultData(o, o2, o3, o4, o5, o6, o7, o8);
                }
            });
        } else {
            zip = Observable.zip(observables[0].compose(handleResult()), observables[1].compose(handleResult()), observables[2].compose(handleResult()), observables[3].compose(handleResult()), observables[4].compose(handleResult()), observables[5].compose(handleResult()), observables[6].compose(handleResult()), observables[7].compose(handleResult()), observables[8].compose(handleResult()), new Function9<Object, Object, Object, Object, Object, Object, Object, Object, Object, MultiResultData>() {
                @Override
                public MultiResultData apply(Object o, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) throws Exception {
                    return new MultiResultData(o, o2, o3, o4, o5, o6, o7, o8, o9);
                }
            });
        }
        if (mView != null && mView.bindToUntilEvent(event) != null) {
            zip = zip.compose(mView.bindToUntilEvent(event));
        }
        zip.doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                if (isShowProgress) {
                    progressSubscribe.showProgress(progress);
                }
                progressSubscribe.preLoad();
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(progressSubscribe);
    }

    public void multiToSubscribe(final ProgressSubscribe progressSubscribe, RxView mView, Object event, Observable... observables) {
        multiToSubscribe(false, null, mView, event, progressSubscribe, observables);
    }
}
