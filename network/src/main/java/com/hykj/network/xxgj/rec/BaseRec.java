package com.hykj.network.xxgj.rec;

/**
 * 接口返回数据基类
 */
public class BaseRec<T> {
    /**
     * code : 0
     * msg : 成功 不需要row跟total，因为这个project已经有另一个PageRec了
     */
    private Integer code;
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
