package com.hykj.network.bjzhdj.rec;

/**
 * created by cjf
 * on:2019/2/27 17:15
 * 分页数据获取返回
 */
public class PageData<T> {
    private T list;
    private Integer total;

    public PageData(T list, Integer total) {
        this.list = list;
        this.total = total;
    }

    public T getList() {
        return list;
    }

    public Integer getTotal() {
        return total;
    }
}
