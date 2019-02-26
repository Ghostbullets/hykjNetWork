package com.hykj.hykjnetwork;

/**
 * created by cjf
 * on:2019/2/26 10:30
 */
public class RequestApi {
    private static Api api;

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
