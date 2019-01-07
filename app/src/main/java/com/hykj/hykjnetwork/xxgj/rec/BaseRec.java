package com.hykj.hykjnetwork.xxgj.rec;

/**
 * 接口返回数据基类
 */
public class BaseRec<T> {
    /**
     * code : 0
     * msg : 成功
     */
    private int code;
    private String msg;
    private T data;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
}
