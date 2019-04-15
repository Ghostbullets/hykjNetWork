package com.hykj.network.rxjava.http;

import com.hykj.network.rxjava.port.AbsTransformer;
import com.hykj.network.rxjava.port.ApplyTransformer;
import com.hykj.network.rxjava.port.IntervalCallBack;
import com.hykj.network.rxjava.port.RepeatWhenCallBack;
import com.hykj.network.rxjava.port.RetryWhenCallBack;
import com.hykj.network.rxjava.port.RxView;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * created by cjf
 * on:2019/3/11 20:18
 * 用于嵌套请求(举例:调用登录接口后如果登陆成功，接着调用获取用户信息接口)、网络请求异常轮询、网络请求条件轮询、网络请求限定次数轮询等
 * <p>
 * 请注意{@link Builder#interval(long, long, TimeUnit, int, IntervalCallBack) } {@link Builder#retryWhen(int, RetryWhenCallBack)}
 * 上面两个可以叠加使用，使用效果是:
 * 当网络请求异常时，会重复请求pollingSize异常轮询次数，当网络请求正常时，会重复请求count条件轮询次数
 * <p>
 * <p>
 * 请注意{@link Builder#interval(long, long, TimeUnit, int, IntervalCallBack)}{@link Builder#apply(ApplyTransformer)}
 * 上面两个叠加使用的话{@link EasyHttp#mObservable}、{@link ApplyTransformer#apply(Object)}都会重复请求count条件轮询次数
 * <p>
 * {@link Builder#repeatWhen(int, RepeatWhenCallBack)}
 * 三个轮询不要重复叠加使用，叠加使用会有什么问题暂时不知道
 */
public class EasyHttp {
    private static final int ERROR_IDENTIFIER = -1;//轮询终止标示
    private Observable mObservable;
    private boolean showProgress;
    private String progress;

    private EasyHttp(Builder builder) {
        mObservable = builder.observable;
        showProgress = builder.showProgress;
        progress = builder.progress;
    }

    public void toSubscribe(RxView mView, Object event, final ProgressSubscribe progressSubscribe) {
        if (mView != null && mView.bindToUntilEvent(event) != null) {
            mObservable = mObservable.compose(mView.bindToUntilEvent(event));
        }
        mObservable.doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                if (showProgress)
                    progressSubscribe.showProgress(progress);
            }
        }).subscribe(progressSubscribe);
    }

    public static final class Builder {
        private Observable observable;
        private AbsTransformer transformer;
        private boolean showProgress;
        private String progress;

        public Builder(Observable observable, AbsTransformer transformer) {
            this.transformer = transformer;
            this.observable = observable.compose(HttpInterface.handleResult(transformer));
        }

        /**
         * 网络请求条件轮询
         *
         * @param initialDelay 初始化延时多久开始请求
         * @param period       间隔多久轮询一次
         * @param unit         时间单位
         * @param count        轮询次数
         * @return
         */
        public Builder interval(long initialDelay, long period, TimeUnit unit, int count, final IntervalCallBack callBack) {
            if (this.observable == null) {
                return this;
            } else {
                final Observable temp = this.observable;
                this.observable = Observable.interval(initialDelay, period, unit).take(Math.max(1, count)).flatMap(new Function<Long, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Long aLong) throws Exception {
                        return temp;
                    }
                }).takeUntil(new Predicate<Object>() {
                    @Override
                    public boolean test(Object o) throws Exception {
                        //如果条件满足，就会终止轮询，这里逻辑可以自己写
                        //结果为true，说明满足条件了，就不在轮询了
                        return callBack != null && callBack.takeUntil(o);
                    }
                });
                return this;
            }
        }

        /**
         * 网络请求限定次数轮询，当调用{@link Observer#onComplete()}方法时会进入apply(Object o)方法，在此判断是否终止轮询
         *
         * @param pollingSize 轮询次数上限,最小为1,如果为0则直接走onComplete()方法
         * @param callBack    回调
         * @return
         */
        public Builder repeatWhen(int pollingSize, final RepeatWhenCallBack callBack) {
            pollingSize = Math.max(1, pollingSize);
            final int count = pollingSize;
            this.observable = this.observable.repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {
                @Override
                public ObservableSource<?> apply(Observable<Object> objectObservable) throws Exception {
                    final AtomicInteger counter = new AtomicInteger();
                    return objectObservable.takeWhile(new Predicate<Object>() {
                        @Override
                        public boolean test(Object o) throws Exception {
                            return counter.getAndIncrement() != count - 1;//不-1的话，会请求1次以后，再重复请求pollingSize次，会多出来一次
                        }
                    }).flatMap(new Function<Object, ObservableSource<?>>() {
                        @Override
                        public ObservableSource<?> apply(Object o) throws Exception {
                            long delay = 5 * 1000;
                            if (callBack != null) {
                                delay = Math.max(0, callBack.disposeTimer(counter.get()));
                            }
                            if (counter.get() == count)//请求完轮询次数，则不延迟发送Observable走onComplete()方法
                                delay = 0;
                            return Observable.timer(delay, TimeUnit.MILLISECONDS);
                        }
                    });
                    //rxjava2.1.8以后建议不要使用Observable.range(1, count)https://github.com/ReactiveX/RxJava/issues/5772
                 /*   return objectObservable.zipWith(Observable.range(1, count), new BiFunction<Object, Integer, Integer>() {
                        @Override
                        public Integer apply(Object o, Integer index) throws Exception {
                            return index;
                        }
                    }).flatMap(new Function<Integer, ObservableSource<?>>() {
                        @Override
                        public ObservableSource<?> apply(Integer index) throws Exception {
                            long delay = 5 * 1000;
                            if (callBack != null) {
                                delay = Math.max(0, callBack.disposeTimer(index));
                            }
                            if (index == count)//请求完轮询次数，则不延迟发送Observable走onComplete()方法
                                delay = 0;
                            return Observable.timer(delay, TimeUnit.MILLISECONDS);
                        }
                    });*/
                }
            });
            return this;
        }

        /**
         * 网络请求异常轮询，当调用{@link Observer#onError(Throwable)} ()}方法时判断是否终止轮询
         *
         * @param pollingSize 轮询次数上限
         * @param callBack    回调
         * @return
         */
        public Builder retryWhen(int pollingSize, final RetryWhenCallBack callBack) {
            pollingSize = Math.max(1, pollingSize);
            final int count = pollingSize;
            this.observable = this.observable.retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                @Override
                public ObservableSource<?> apply(Observable<Throwable> errors) throws Exception {
                    final AtomicInteger counter = new AtomicInteger();
                    return errors.takeWhile(new Predicate<Throwable>() {
                        @Override
                        public boolean test(Throwable throwable) throws Exception {
                            if (callBack != null && callBack.disposeThrowable(throwable)) {//返回true直接终止轮询，false继续轮询直到次数到上限
                                return true;
                            }
                            //返回true结束轮询
                            return counter.getAndIncrement() != count - 1;//得到counter的值，跟count-1比较，然后将counter里面的值+1
                        }
                    }).flatMap(new Function<Throwable, ObservableSource<?>>() {
                        @Override
                        public ObservableSource<?> apply(Throwable throwable) throws Exception {
                            long delay = 5 * 1000;
                            if (callBack != null) {
                                delay = Math.max(0, callBack.disposeTimer(counter.get()));
                            }
                            if (counter.get() == count)//请求完轮询次数，则不延迟发送Observable走onComplete()方法
                                delay = 0;
                            return Observable.timer(delay, TimeUnit.MILLISECONDS);
                        }
                    });
                  /*  return errors.zipWith(Observable.range(1, count), new BiFunction<Throwable, Integer, ErrorData>() {
                        @Override
                        public ErrorData apply(Throwable throwable, Integer index) throws Exception {
                            if (callBack != null && callBack.disposeThrowable(throwable)) {//返回true直接终止轮询，false继续轮询直到次数到上限
                                index = ERROR_IDENTIFIER;
                            }
                            return new ErrorData(throwable, index);
                        }
                    }).flatMap(new Function<ErrorData, ObservableSource<?>>() {
                        @Override
                        public ObservableSource<?> apply(ErrorData errorData) throws Exception {
                            if (errorData.index == ERROR_IDENTIFIER) {
                                return Observable.error(errorData.t);
                            }
                            long delay = 5 * 1000;
                            if (callBack != null) {
                                delay = Math.max(0, callBack.disposeTimer(errorData.index));
                            }
                            if (errorData.index == count)//请求完轮询次数，则不延迟发送Observable走onComplete()方法
                                delay = 0;
                            return Observable.timer(delay, TimeUnit.MILLISECONDS);
                        }
                    });*/
                }
            });
            return this;
        }

        //返回一个可观察的对象，该对象基于您提供的对象数据产生(举例:登录后需要获取用户信息，调用该方法根据登录时获取到的token来获取用户信息)
        public Builder apply(final ApplyTransformer transformer) {
            this.observable = observable.compose(new ObservableTransformer() {
                @Override
                public ObservableSource apply(Observable upstream) {
                    return upstream.flatMap(new Function() {
                        @Override
                        public Object apply(Object o) throws Exception {
                            return transformer.apply(o).compose(HttpInterface.handleResult(Builder.this.transformer));
                        }
                    });
                }
            });
            return this;
        }

        public Builder isShowProgress(boolean val) {
            this.showProgress = val;
            return this;
        }

        public Builder progress(String val) {
            this.progress = val;
            return this;
        }

        public EasyHttp build() {
            return new EasyHttp(this);
        }
    }

  /*  public static class ErrorData {
        private Throwable t;//错误对象
        private int index;//错误在轮询中的位置

        public ErrorData(Throwable t, int index) {
            this.t = t;
            this.index = index;
        }

        public Throwable getT() {
            return t;
        }

        public int getIndex() {
            return index;
        }
    }*/
}
