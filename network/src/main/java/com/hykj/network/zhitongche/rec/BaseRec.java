package com.hykj.network.zhitongche.rec;

import java.util.List;

/**
 * created by cjf
 * on: 2019/8/27
 */
public class BaseRec<T> {
    private Integer code;
    private String msg;
    private Integer total;
    private T data;//对象、或者列表
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
