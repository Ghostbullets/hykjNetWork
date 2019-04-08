package com.hykj.network.rxjava.http;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hykj.network.rxjava.port.AbsTransformer;
import com.hykj.network.utils.Utils;

import java.io.IOException;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络请求处理
 */
public class HttpInterface {

    /**
     * 根据传入的信息生成一个Retrofit对象
     *
     * @param headers 头部信息，选填
     * @param baseUrl 网络请求的完整 Url =该baseUrl加上在网络请求接口的注解设置
     * @return
     */
    public static Retrofit deRequest(final Map<String, String> headers, @NonNull String baseUrl) {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        if (headers != null && !headers.isEmpty()) {
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                @Override
                public Response intercept(@NonNull Chain chain) throws IOException {
                    Request.Builder builder = chain.request().newBuilder();
                    for (Map.Entry<String, String> entry : headers.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        Utils.checkNameAndValue(key, value);
                        builder.addHeader(key, value);
                    }
                    return chain.proceed(builder.build());
                }
            }).build();
            builder.client(client);
        }
        return builder.build();
    }

    /**
     * 将上游的H 转换成T，并再次发送，具体转换由继承AbsTransformer的类来处理(比如：BaseRec<T>转换成另一个xxx)
     * 可参考  {@link com.hykj.network.bjzhdj.http.RxJavaHelper 的handleResult()方法}
     *
     * @param transformer 转换器,如果为null,则直接返回原被观察者持有的泛型
     * @param <H>         原被观察者持有的泛型
     * @param <T>         转换后返回的被观察者持有的泛型
     * @return
     */
    public static <H, T> ObservableTransformer<H, T> handleResult(final AbsTransformer<H, T> transformer) {
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
     * 创建新的Observable并发送解析后的数据
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Observable<T> createData(@Nullable final T data) {
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
