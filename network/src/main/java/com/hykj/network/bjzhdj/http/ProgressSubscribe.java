package com.hykj.network.bjzhdj.http;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.hykj.network.dialog.ProgressBarDialog;
import com.hykj.network.utils.Utils;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * created by cjf
 * on:2019/2/26 11:01
 * 处理RxJava+retrofit回调
 */
public abstract class ProgressSubscribe<T> implements ProgressBarDialog.ProgressCancelListener, Observer<T> {
    protected static final String TAG = ProgressSubscribe.class.getName();
    protected FragmentActivity mActivity;
    protected Disposable disposable;
    protected ProgressBarDialog mHub;

    public ProgressSubscribe(FragmentActivity activity) {
        this.mActivity = activity;
        mHub = new ProgressBarDialog().init(activity);
    }

    @Override
    public void onSubscribe(Disposable d) {
        this.disposable = d;
    }

    @Override
    public void onComplete() {
        mHub.dismiss();
    }

    @Override
    public void onError(Throwable e) {
        Log.e(TAG, e.getMessage());
        if (!Utils.isNetWorkConnected(mActivity)) {
            Toast.makeText(mActivity, "网络不可用", Toast.LENGTH_SHORT).show();
        } else {
            onFailure(e);
        }
        mHub.dismiss();
    }

    @Override
    public void onNext(T t) {
        onResponse(t);
    }

    @Override
    public void onCancelListener() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    /**
     * 显示进度条
     */
    public void showProgress(String message) {
        if (mHub != null) {
            mHub.showProgress(message);
        }
    }

    protected abstract void onResponse(T t);

    protected void onFailure(Throwable e) {

    }
}
