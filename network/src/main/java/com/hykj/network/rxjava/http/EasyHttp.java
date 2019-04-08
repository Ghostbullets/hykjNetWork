package com.hykj.network.rxjava.http;

import com.hykj.network.rxjava.port.AbsTransformer;
import com.hykj.network.rxjava.port.ApplyTransformer;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * created by cjf
 * on:2019/3/11 20:18
 * 用于嵌套请求(举例:调用登录接口后如果登陆成功，接着调用获取用户信息接口)
 */
public class EasyHttp {
    private Observable mObservable;
    private boolean showProgress;
    private String progress;

    private EasyHttp(Builder builder) {
        mObservable = builder.observable;
        showProgress = builder.showProgress;
        progress = builder.progress;
    }

    public void toSubscribe(final ProgressSubscribe progressSubscribe) {
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
}
