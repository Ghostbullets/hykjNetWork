package com.hykj.network.bjzhdj.rec;

/**
 * 接口返回数据基类
 */
public class BaseRec<T> {
    /**
     * code : 0
     * msg : 成功
     * total: 分页数据在数据库中的总数
     */
    private int code;
    private String msg;
    private Integer total;
    private T data;
    private T rows;
    private Integer role;

    public int getCode() {
        return code;
    }

    public Integer getTotal() {
        return total;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public T getRows() {
        return rows;
    }

    public Integer getRole() {
        return role;
    }
}
