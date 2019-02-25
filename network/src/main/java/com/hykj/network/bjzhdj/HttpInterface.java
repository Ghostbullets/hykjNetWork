package com.hykj.network.bjzhdj;

import android.support.annotation.NonNull;
import com.hykj.network.utils.Utils;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

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
        if (headers != null) {
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                @Override
                public Response intercept(@NonNull Chain chain) throws IOException {
                    Request.Builder builder = chain.request().newBuilder();
                    Iterator<Map.Entry<String, String>> iterator = headers.entrySet().iterator();
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
}
