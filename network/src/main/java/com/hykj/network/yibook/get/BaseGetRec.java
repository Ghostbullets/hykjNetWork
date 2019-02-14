package com.hykj.network.yibook.get;

/**
 * get请求返回数据接收
 * @param <T>
 */
public class BaseGetRec<T> {
    /**
     * status : 0
     * msg : 成功
     */
    private int status;
    private String msg;
    private T data;
    private int totalCount;

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public int getTotalCount() {
        return totalCount;
    }
}
