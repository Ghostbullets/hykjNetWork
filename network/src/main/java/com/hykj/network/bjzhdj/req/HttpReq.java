package com.hykj.network.bjzhdj.req;


import android.os.Handler;
import android.os.Looper;

import com.hykj.network.bjzhdj.callback.ObtainCallBack;
import com.hykj.network.utils.ReflectUtils;
import com.hykj.network.utils.Utils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 网络请求基类
 */
public abstract class HttpReq {
    private String httpUrl;
    private Map<String, String> headers = new LinkedHashMap<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public HttpReq(String url) {
        this.httpUrl = url;
    }

    /**
     * @param callBack 回调
     */
    public void doRequest(final ObtainCallBack callBack) {
        Map<String, String> params = new LinkedHashMap<>();
        ReflectUtils.progressData(params, this, HttpReq.class);

        //添加主体数据
        MultipartBody.Builder body = new MultipartBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            body.addPart(Headers.of("Content-Disposition", "form-data;name=\"" + entry.getKey() + "\""), RequestBody.create(null, entry.getValue()));
        }
        Request.Builder builder = new Request.Builder();
        //添加头部数据
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
        Request request = builder.post(body.build()).url(httpUrl).build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onFailure(e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body() != null) {
                    final Object rec = callBack.parseNetworkResponse(response.body().string());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onResponse(rec);
                            callBack.onFinish();
                        }
                    });
                }
            }
        });
    }

    public Observable doRequest() {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                Map<String, String> params = new LinkedHashMap<>();
                ReflectUtils.progressData(params, HttpReq.this, HttpReq.class);

                //添加主体数据
                MultipartBody.Builder body = new MultipartBody.Builder();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    body.addPart(Headers.of("Content-Disposition", "form-data;name=\"" + entry.getKey() + "\""), RequestBody.create(null, entry.getValue()));
                }
                Request.Builder builder = new Request.Builder();
                //添加头部数据
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    builder.addHeader(entry.getKey(), entry.getValue());
                }
                Request request = builder.post(body.build()).url(httpUrl).build();
                new OkHttpClient().newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        emitter.onError(e);
                        emitter.onComplete();
                        call.cancel();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.body() != null) {
                            emitter.onNext(response.body().string());
                            emitter.onComplete();
                        }
                        call.cancel();
                    }
                });
            }
        });
    }

    /**
     * 添加头部参数
     *
     * @param headers 参数集
     */
    public <H extends HttpReq> H addHeaders(Map<String, String> headers) {
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                Utils.checkNameAndValue(key, value);
                this.headers.put(key, value);
            }
        }
        return (H) this;
    }

    /**
     * 添加单个头部参数
     *
     * @param key   键
     * @param value 值
     */
    public <H extends HttpReq> H addHeader(String key, String value) {
        Utils.checkNameAndValue(key, value);
        this.headers.put(key, value);
        return (H) this;
    }
}
