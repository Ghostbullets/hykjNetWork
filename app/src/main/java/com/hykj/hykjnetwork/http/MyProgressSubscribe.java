package com.hykj.hykjnetwork.http;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.base.network.rxjava.http.ProgressSubscribe;
import com.hykj.network.bjzhdj.http.ApiException;
import com.hykj.network.bjzhdj.rec.PageData;

import org.jetbrains.annotations.Nullable;

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

    public MyProgressSubscribe() {
    }

    public MyProgressSubscribe(@Nullable FragmentActivity activity) {
        super(activity);
        dialog = new MyProgressBarDialog().init(activity);
    }

    public MyProgressSubscribe(@Nullable Fragment fragment) {
        super(fragment);
    }

    public MyProgressSubscribe(FragmentActivity activity, PageInfo pageInfo) {
        super(activity);
        this.pageInfo = pageInfo;
        dialog = new MyProgressBarDialog().init(activity);
    }

    public MyProgressSubscribe(Fragment fragment, PageInfo pageInfo) {
        super(fragment);
        this.pageInfo = pageInfo;
        dialog = new MyProgressBarDialog().init(fragment.getActivity());
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
    public void onFailure(Throwable e) {
        super.onFailure(e);
//        if (e instanceof ApiException && b.get() != null) {
//            //VerifyCodeUtils.dispose(mActivity.get(), ((ApiException) e).getErrorRec(), needLogin);
//        } else {
//            //Tip.showShort(e.getMessage());
//        }
        if (e instanceof ApiException && getFragment() != null && getFragment().get() != null) {
            //VerifyCodeUtils.dispose(getMFragment().get(), ((ApiException) e).getErrorRec());
        } else if (e instanceof ApiException && getActivity() != null && getActivity().get() != null) {
            //VerifyCodeUtils.dispose(getMActivity().get(), ((ApiException) e).getErrorRec());
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
    public void onFinish() {
        super.onFinish();
        //自定义的一定要写这个方法
        //RxJavaHelper.getInstance().setFailResultObject(false);
        if (pageInfo != null)//设置加载结束
            pageInfo.setLoading(false);
    }

    @Override
    public void showProgress(@Nullable CharSequence message) {
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
