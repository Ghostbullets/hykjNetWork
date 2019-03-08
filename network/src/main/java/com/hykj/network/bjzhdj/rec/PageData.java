package com.hykj.network.bjzhdj.rec;

/**
 * created by cjf
 * on:2019/2/27 17:15
 * 分页数据获取返回
 */
public class PageData<T> {
    private T list;
    private Integer total;//列表总数
    private Integer role;//权限 1有0没有

    public PageData(T list, Integer total) {
        this.list = list;
        this.total = total;
    }

    public PageData(T list, Integer total, Integer role) {
        this.list = list;
        this.total = total;
        this.role = role;
    }

    public T getList() {
        return list;
    }

    public Integer getTotal() {
        return total;
    }

    public Integer getRole() {
        return role;
    }
}
