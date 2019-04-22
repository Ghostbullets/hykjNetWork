package com.hykj.network.tsw.rec;

/**
 * 接口返回数据基类
 */
public class BaseRec<T> {
    /**
     * code : 0
     * msg : 成功
     */
    private int status;
    private T result;
    private Integer hasNext;//是否还有下一页，0没有，1有
    private Integer totalcount;//总条数

    public int getStatus() {
        return status;
    }

    public T getResult() {
        return result;
    }

    public Integer getHasNext() {
        return hasNext;
    }

    public Integer getTotalcount() {
        return totalcount;
    }

    public boolean isHasNext() {
        return hasNext != null && hasNext != 0;
    }
}
