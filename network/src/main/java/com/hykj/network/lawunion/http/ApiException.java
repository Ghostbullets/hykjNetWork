package com.hykj.network.lawunion.http;

import android.text.TextUtils;

import com.hykj.network.lawunion.rec.BaseRec;

/**
 * created by cjf
 * on: 2019/8/9
 */
public class ApiException extends RuntimeException {
    private BaseRec errorRec;

    public ApiException(BaseRec errorRec) {
        super(errorRec != null && !TextUtils.isEmpty(errorRec.getMsg()) ? errorRec.getMsg() : "服务端错误");
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
