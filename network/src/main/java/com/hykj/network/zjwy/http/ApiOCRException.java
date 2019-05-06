package com.hykj.network.zjwy.http;


import android.text.TextUtils;

import com.hykj.network.zjwy.rec.OCRRec;

/**
 * created by cjf
 * on:2019/2/26 14:29
 * 云脉的网络请求异常信息具体实现类
 */
public class ApiOCRException extends RuntimeException {
    private OCRRec errorRec;

    public ApiOCRException(OCRRec errorRec) {
        super(errorRec != null && !TextUtils.isEmpty(errorRec.getInfo()) ? errorRec.getInfo() : "服务端错误");
        this.errorRec = errorRec;
    }

    public OCRRec getErrorRec() {
        return errorRec;
    }

    public String getMessage() {
        return errorRec.getInfo();
    }

    public String getStatus() {
        return errorRec.getStatus();
    }

    public boolean isSuccess() {
        return "OK".equals(errorRec.getStatus());
    }
}
