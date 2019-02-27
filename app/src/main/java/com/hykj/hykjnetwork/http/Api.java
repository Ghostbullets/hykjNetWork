package com.hykj.hykjnetwork.http;

import com.hykj.network.bjzhdj.rec.BaseRec;

import io.reactivex.Observable;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * created by cjf
 * on:2019/2/26 10:20
 */
public interface Api {
    @FormUrlEncoded
    @POST("xxx.xxx")
    Observable<BaseRec<String>> login();
}
