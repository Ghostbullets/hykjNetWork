package com.hykj.network.zjwy.http;


import com.hykj.network.zjwy.rec.BaseRec;

/**
 * created by cjf
 * on:2019/2/26 14:29
 * 该项目的网络请求异常信息具体实现类
 */
public class ApiException extends RuntimeException {
    private BaseRec errorRec;

    public ApiException(BaseRec errorRec) {
        this.errorRec = errorRec;
    }

    public BaseRec getErrorRec() {
        return errorRec;
    }

    public String getMessage() {
        return errorRec.getMessage();
    }

    public boolean isSuccess() {
        return errorRec.isSuccess();
    }
}
