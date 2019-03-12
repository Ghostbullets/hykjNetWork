package com.hykj.hykjnetwork.http;

import android.support.v4.app.FragmentActivity;

import com.hykj.network.bjzhdj.http.ApiException;
import com.hykj.network.bjzhdj.http.ProgressSubscribe;
import com.hykj.network.bjzhdj.rec.PageData;

/**
 * created by cjf
 * on:2019/2/26 15:28
 * 自定义网络请求返回结果处理
 */
public abstract class MyProgressSubscribe<T> extends ProgressSubscribe<T> {
    private boolean needLogin = true;
    private PageInfo pageInfo;//用于分页结束数据

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public MyProgressSubscribe(FragmentActivity activity) {
        super(activity);
    }

    public MyProgressSubscribe(FragmentActivity activity, PageInfo pageInfo) {
        super(activity);
        this.pageInfo = pageInfo;
    }

    public MyProgressSubscribe<T> setNeedLogin(boolean needLogin) {
        this.needLogin = needLogin;
        return this;
    }

    public MyProgressSubscribe<T> setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
        return this;
    }

    @Override
    protected void onFailure(Throwable e) {
        super.onFailure(e);
        if (e instanceof ApiException && mActivity.get() != null) {
            //VerifyCodeUtils.dispose(mActivity.get(), ((ApiException) e).getErrorRec(), needLogin);
        } else {
            //Tip.showShort(e.getMessage());
        }
    }

    @Override
    public void onNext(T t) {
        super.onNext(t);
        if (pageInfo != null && t instanceof PageData)//设置是否还有下一页
            pageInfo.setHasNext(((PageData) t).getTotal());
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        if (pageInfo != null)//设置加载结束
            pageInfo.setLoading(false);
    }
}
