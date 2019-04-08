package com.hykj.network.zjwy.rec;

/**
 * 该项目的接口返回数据基类
 */
public class BaseRec<T> {
    /**
     * success : true成功 false失败
     * message : 成功
     */
    private String success;
    private String message;
    private T data;

    public String getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public boolean isSuccess() {
        return "true".equals(success);
    }
}
