package com.hykj.network.ags.rec;

import java.util.List;

/**
 * created by cjf
 * on:2019/2/27 17:15
 * 该项目的分页数据获取返回(暂无)
 */
public class PageData<T> {
    private List<T> list;
    private Integer total;//列表总数

    public PageData(List<T> list, Integer total) {
        this.list = list;
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public Integer getTotal() {
        return total;
    }
}
