package com.hykj.hykjnetwork;

import com.hykj.network.bjzhdj.ApiFactoryAbs;

/**
 * created by cjf
 * on:2019/2/26 10:19
 */
public class ApiFactory extends ApiFactoryAbs<Api> {
    public ApiFactory() {
        super("http://www.baidu.com");
    }
}
