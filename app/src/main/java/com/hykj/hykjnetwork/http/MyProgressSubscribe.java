package com.hykj.hykjnetwork.http;

import android.support.v4.app.FragmentActivity;

import com.hykj.network.bjzhdj.http.ApiException;
import com.hykj.network.bjzhdj.http.ProgressSubscribe;
import com.hykj.network.bjzhdj.rec.PageData;
import com.hykj.network.zjwy.http.RxJavaHelper;

/**
 * created by cjf
 * on:2019/2/26 15:28
 * 自定义网络请求返回结果处理
 */
public abstract class MyProgressSubscribe<T> extends ProgressSubscribe<T> {
    private boolean needLogin = true;
    private PageInfo pageInfo;//用于分页结束数据
    private MyProgressBarDialog dialog;//使用这种方法替换弹窗

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public MyProgressSubscribe(FragmentActivity activity) {
        super(activity);
        dialog=new MyProgressBarDialog().init(activity);
    }

    public MyProgressSubscribe(FragmentActivity activity, PageInfo pageInfo) {
        super(activity);
        this.pageInfo = pageInfo;
        dialog=new MyProgressBarDialog().init(activity);
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
        //自定义的一定要写这个方法
        RxJavaHelper.getInstance().setFailResultObject(false);
        if (pageInfo != null)//设置加载结束
            pageInfo.setLoading(false);
    }

    @Override
    public void showProgress(String message) {
        //super.showProgress(message);
        dialog.showProgress(message);
    }

    @Override
    public void onComplete() {
        super.onComplete();
        dialog.dismiss();
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
        dialog.dismiss();
    }
}
