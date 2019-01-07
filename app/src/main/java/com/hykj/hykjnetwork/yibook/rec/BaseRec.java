package com.hykj.hykjnetwork.yibook.rec;

/**
 * 壹账本接口返回数据基类
 */
public class BaseRec<T> {
    /**
     * status : 0
     * msg : 成功
     */
    private int status;
    private T result;

    public int getStatus() {
        return status;
    }

    public T getResult() {
        return result;
    }
}
