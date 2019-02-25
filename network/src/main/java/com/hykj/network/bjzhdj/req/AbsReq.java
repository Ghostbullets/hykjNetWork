package com.hykj.network.bjzhdj.req;

import android.support.annotation.NonNull;

import com.hykj.network.bjzhdj.HttpInterface;
import com.hykj.network.bjzhdj.callback.ObtainCallBack;
import com.hykj.network.utils.ReflectUtils;
import com.hykj.network.utils.Utils;

import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 网络请求基类的抽象类
 */
public abstract class AbsReq<H> {
    private String baseUrl;
    private Class<H> service;
    private Map<String, String> headers = new LinkedHashMap<>();//头布局集合参数，由继承AbsReq的类选择是否设置
    private Disposable disposable;

    public AbsReq(String baseUrl) {
        this.baseUrl = baseUrl;
        Class<? extends AbsReq> cls = getClass();
        while (!(cls.getGenericSuperclass() instanceof ParameterizedType)) {
            cls = (Class<? extends AbsReq>) cls.getSuperclass();
        }
        ParameterizedType type = (ParameterizedType) cls.getGenericSuperclass();
        if (type != null && type.getActualTypeArguments().length > 0) {
            this.service = (Class<H>) type.getActualTypeArguments()[0];
        }
    }

    /**
     * 初始化，得到对应的网络请求的接口
     *
     * @return
     */
    public H init() {
        return HttpInterface.deRequest(headers, baseUrl).create(service);
    }

    /**
     * 网络请求
     *
     * @param observable 被观察者
     * @param callBack   回调
     */
    public void doRequest(Observable observable, @NonNull final ObtainCallBack callBack) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Object o) {
                        callBack.onResponse(o);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onFailure(e);
                    }

                    @Override
                    public void onComplete() {
                        callBack.onFinish();
                    }
                });
    }

    /**
     * 得到继承AbsReq的类所定义的属性名、属性值map对象参数(注：不包含AbsReq内定义的参数)
     *
     * @return
     */
    public Map<String, String> getParams() {
        return ReflectUtils.progressData(null, this, AbsReq.class);
    }

    /**
     * 添加头部参数
     *
     * @param headers 参数集
     */
    public <T extends AbsReq> T addHeaders(Map<String, String> headers) {
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                Utils.checkNameAndValue(key, value);
                this.headers.put(key, value);
            }
        }
        return (T) this;
    }

    /**
     * 添加单个头部参数
     *
     * @param key   键
     * @param value 值
     */
    public <T extends AbsReq> T addHeader(String key, String value) {
        Utils.checkNameAndValue(key, value);
        this.headers.put(key, value);
        return (T) this;
    }

    public void dispose() {
        if (!disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
