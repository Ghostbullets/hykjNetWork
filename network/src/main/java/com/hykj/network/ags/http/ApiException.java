package com.hykj.network.ags.http;

import com.hykj.network.ags.rec.BaseRec;

/**
 * created by cjf
 * on:2019/4/11 19:20
 */
public class ApiException extends RuntimeException {
    private BaseRec errorRec;

    public ApiException(BaseRec errorRec) {
        this.errorRec = errorRec;
    }

    public BaseRec getErrorRec() {
        return errorRec;
    }

    public String getMsg() {
        return errorRec.getMsg();
    }

    public Integer getCode() {
        return errorRec.getCode();
    }
}
