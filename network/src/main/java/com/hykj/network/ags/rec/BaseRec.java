package com.hykj.network.ags.rec;

import java.util.List;

/**
 * created by cjf
 * on:2019/4/11 19:14
 * 返回数据
 */
public class BaseRec<T> {
    private Integer code;//状态码
    private String msg;//描述
    private Integer total;//列表数据总条数
    private T data;//普通数据
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
