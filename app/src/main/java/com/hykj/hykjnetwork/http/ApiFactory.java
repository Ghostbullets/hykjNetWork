package com.hykj.hykjnetwork.http;

import com.hykj.network.bjzhdj.http.ApiFactoryAbs;

/**
 * created by cjf
 * on:2019/2/26 10:19
 * 网络请求工具类的使用
 */
public class ApiFactory extends ApiFactoryAbs<Api> {
    private static Api api;

    private ApiFactory() {
        super("http://www.baidu.com");
    }

    //单例模式，全局唯一
    public static Api getInstance() {
        synchronized (ApiFactory.class) {
            if (api == null) {
                api = new ApiFactory().init();
            }
            return api;
        }
    }
}
