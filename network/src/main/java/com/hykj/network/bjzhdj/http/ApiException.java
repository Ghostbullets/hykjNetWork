package com.hykj.network.bjzhdj.http;

import com.hykj.network.bjzhdj.rec.BaseRec;

/**
 * created by cjf
 * on:2019/2/26 14:29
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
        return errorRec.getMsg();
    }

    public int getCode() {
        return errorRec.getCode();
    }
}
