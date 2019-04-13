package com.hykj.network.rxjava.port;

/**
 * created by cjf
 * on:2019/4/13 11:49
 * 使用{@link com.hykj.network.rxjava.http.EasyHttp.Builder#retryWhen(int, RetryWhenCallBack)}方法回调
 */
public interface RetryWhenCallBack {
    /**
     * 处理网络请求异常
     *
     * @param t
     * @return 返回true终止轮询，false继续轮询
     */
    boolean disposeThrowable(Throwable t);

    /**
     * @param index 当前网络请求异常在轮询中的位置
     * @return 延迟多少毫秒重试网络请求
     */
    long disposeTimer(int index);
}
