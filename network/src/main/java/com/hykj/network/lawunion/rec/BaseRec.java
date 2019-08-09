package com.hykj.network.lawunion.rec;

import java.util.List;

/**
 * created by cjf
 * on: 2019/8/9
 * 返回数据
 */
public class BaseRec<T> {
    private Integer code;
    private String msg;
    private Integer total;
    private T data;
    private List<T> rows;//列表数据

    public Integer getCode() {
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

    public List<T> getRows() {
        return rows;
    }
}
