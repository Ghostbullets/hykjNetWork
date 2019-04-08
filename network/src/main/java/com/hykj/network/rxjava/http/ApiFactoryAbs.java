package com.hykj.network.rxjava.http;

import com.hykj.network.utils.Utils;

import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * created by cjf
 * on:2019/2/26 10:06
 * 继承该类，传入泛型的接口类用于retrofit
 */
public class ApiFactoryAbs<H> {
    private Class<H> service;
    private String baseUrl;
    private Map<String, String> headers = new LinkedHashMap<>();//头布局集合参数，由继承AbsReq的类选择是否设置

    public ApiFactoryAbs(String baseUrl) {
        this.baseUrl = baseUrl;
        Class<? extends ApiFactoryAbs> cls = getClass();
        while (!(cls.getGenericSuperclass() instanceof ParameterizedType)) {
            cls = (Class<? extends ApiFactoryAbs>) cls.getSuperclass();
        }
        ParameterizedType type = (ParameterizedType) cls.getGenericSuperclass();
        if (type != null && type.getActualTypeArguments().length > 0) {
            this.service = (Class<H>) type.getActualTypeArguments()[0];
        }
    }

    /**
     * 添加头部参数
     *
     * @param headers 参数集
     */
    public <T extends ApiFactoryAbs> T addHeaders(Map<String, String> headers) {
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
    public <T extends ApiFactoryAbs> T addHeader(String key, String value) {
        Utils.checkNameAndValue(key, value);
        this.headers.put(key, value);
        return (T) this;
    }

    /**
     * 初始化，得到对应的网络请求的接口
     *
     * @return
     */
    public H init() {
        return HttpInterface.deRequest(headers, baseUrl).create(service);
    }
}
