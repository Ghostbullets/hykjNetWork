package com.hykj.network.xxgj.rec;

/**
 * 接口返回数据基类
 */
public class BaseRec<T> {
    /**
     * code : 0
     * msg : 成功
     */
    private Integer code;
    private String msg;
    private Integer total;
    private T data;
    private T rows;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Integer getTotal() {
        return total;
    }

    public T getData() {
        return data;
    }

    public T getRows() {
        return rows;
    }
}
