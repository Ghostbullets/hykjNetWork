package com.hykj.network.rxjava.req;

import com.google.gson.Gson;

import com.hykj.network.rxjava.http.HttpInterface;
import com.hykj.network.utils.ReflectUtils;
import com.hykj.network.utils.Utils;

import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * 网络请求基类的抽象类
 */
public abstract class AbsReq<H> {
    private String baseUrl;
    private Class<H> service;
    private Map<String, String> headers = new LinkedHashMap<>();//头布局集合参数，由继承AbsReq的类选择是否设置

    public AbsReq(String baseUrl) {
        this.baseUrl = baseUrl;
        Class<? extends AbsReq> cls = getClass();
        while (!(cls.getGenericSuperclass() instanceof ParameterizedType)) {
            cls = (Class<? extends AbsReq>) cls.getSuperclass();
        }
        if (cls.getGenericSuperclass() instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) cls.getGenericSuperclass();
            if (type != null && type.getActualTypeArguments().length > 0) {
                this.service = (Class<H>) type.getActualTypeArguments()[0];
            }
        }
    }

    /**
     * 初始化，得到对应的网络请求的接口
     *
     * @return
     */
    public H init() {
        return HttpInterface.deRequest(headers, baseUrl,null).create(service);
    }

    /**
     * 初始化，得到对应的网络请求的接口
     *
     * @return
     */
    public H init(OkHttpClient.Builder builder) {
        return HttpInterface.deRequest(headers, baseUrl,builder).create(service);
    }

    /**
     * 得到继承AbsReq的类所定义的属性名、属性值map对象参数(注：不包含AbsReq内定义的参数)
     * 可用于表单请求参数
     *  @FormUrlEncoded
     *  @POST("xxx.xxx")
     *  Observable<BaseRec <String>> login(@FieldMap Map<String, String> map);
     *
     * @return
     */
    public Map<String, String> getParams() {
        return ReflectUtils.progressData(null, this, AbsReq.class);
    }

    /**
     * 可用于Content-Type是application/json时网络请求参数使用
     * 除了在这里加以外，还可以在{@link com.hykj.network.rxjava.http.HttpInterface}里面addHeader("Content-Type", "application/json;charset=utf-8");
     *  @Headers({"Content-Type:application/json;charset=utf-8"})
     *  @POST("xxx.xxx")
     *  Observable<BaseRec < String>> register(@Body RequestBody body);
     *
     * @return
     */
    public RequestBody getRequestBody() {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        Map<String, String> params = getParams();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.addPart(Headers.of("Content-Disposition", "form-data;name=\"" + entry.getKey() + "\""), RequestBody.create(MediaType.parse("application/json"), entry.getValue()));
        }
        return builder.setType(MultipartBody.FORM).build();
    }

    /**
     * 用于 http请求 post(json格式)时使用，普通的http的编码格式也就是mid=10&method=userInfo&dateInt=20160818
     * json编码格式的，也就是编码成{"mid":"10","method":"userInfo","dateInt":"20160818"}
     * @POST("xxx.xxx")
     * Observable<BaseRec < String>> register(@Body RequestBody body);
     * @return
     */
    public RequestBody getJSONBody() {
        return RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(getParams()));
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
}
