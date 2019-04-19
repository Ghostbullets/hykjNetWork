package com.hykj.network.rxjava.port;

/**
 * created by cjf
 * on:2019/4/13 11:49
 * 使用{@link com.hykj.network.rxjava.http.EasyHttp.Builder#repeatWhen(int, RepeatWhenCallBack)}方法时的回调
 */
public interface RepeatWhenCallBack {
    /**
     * 处理网络请求
     * @param index 当前网络请求异常在轮询中的位置
     * @return 返回true终止轮询，false继续轮询
     */
   /* boolean disposeThrowable(int index);*/

    /**
     * @param number 当前网络请求异常是第几次轮询
     * @param count 轮询次数
     * @return 延迟多少毫秒重试网络请求
     */
    long disposeTimer(int number,int count);
}
