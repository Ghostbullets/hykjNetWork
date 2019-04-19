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
     * @param t 异常
     * @param number 当前网络请求异常是第几次轮询,从1下标开始，由于先请求一次以后才走这个方法，所以该值>=1(该值范围1到count)
     * @param count 轮询次数
     * @return 返回true终止轮询，false继续轮询
     */
    boolean disposeThrowable(Throwable t,int number,int count);

    /**
     * @param number 当前网络请求异常是第几次轮询
     * @param count 轮询次数
     * @return 延迟多少毫秒重试网络请求
     */
    long disposeTimer(int number,int count);
}
