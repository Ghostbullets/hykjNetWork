package com.hykj.hykjnetwork.http;

import com.hykj.network.bjzhdj.rec.BaseRec;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * created by cjf
 * on:2019/2/26 10:20
 */
public interface Api {
    //网络请求Content-Type是application/x-www-form-urlencoded时使用
    @FormUrlEncoded
    @POST("xxx.xxx")
    Observable<BaseRec<String>> login(@FieldMap Map<String, String> map);

    //还可以在Retrofit.Builder.client(OkHttpClient)

    /**
     * 网络请求Content-Type是application/json时使用
     * 除了在这里加以外，还可以在{@link com.hykj.network.rxjava.http.HttpInterface}里面addHeader("Content-Type", "application/json;charset=utf-8");
     *
     * @return
     */
    @Headers({"Content-Type:application/json;charset=utf-8"})
    @POST("xxx.xxx")
    Observable<BaseRec<String>> register(@Body RequestBody body);
}
