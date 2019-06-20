package com.hykj.network.rxjava.http;

import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.hykj.network.dialog.ProgressBarDialog;
import com.hykj.network.utils.Utils;

import java.lang.ref.WeakReference;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * created by cjf
 * on:2019/2/26 11:01
 * 处理RxJava+retrofit回调
 */
public abstract class ProgressSubscribe<T> implements ProgressBarDialog.ProgressCancelListener, Observer<T> {
    protected static final String TAG = ProgressSubscribe.class.getName();
    protected WeakReference<FragmentActivity> mActivity;
    protected Disposable disposable;
    protected ProgressBarDialog mHub;
    private Type genericityType;

    public ProgressSubscribe(FragmentActivity activity) {
        this.mActivity = new WeakReference<>(activity);
        mHub = new ProgressBarDialog().init(activity);
        try {
            ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
            if (type != null && type.getActualTypeArguments().length > 0) {
                genericityType = type.getActualTypeArguments()[0];
            }
        } catch (ClassCastException e) {
            genericityType = Object.class;
        }
    }

    @Override
    public void onSubscribe(Disposable d) {
        this.disposable = d;
    }

    @Override
    public void onComplete() {
        onFinish();
        mHub.dismiss();
    }

    @Override
    public void onError(Throwable e) {
        Log.e(TAG, e.getMessage());
        if (mActivity.get() != null && !Utils.isNetWorkConnected(mActivity.get())) {
            Toast.makeText(mActivity.get(), "网络不可用", Toast.LENGTH_SHORT).show();
        } else {
            onFailure(e);
        }
        onFinish();
        mHub.dismiss();
    }

    @Override
    public void onNext(T t) {
        //如果传过来的数据类型不是列表，并且你希望返回列表数据，则返回空数组
        String name = genericityType.toString();
        if (!TextUtils.isEmpty(name) && name.contains("<") && name.contains(">")) {
            name = name.substring(0, name.indexOf("<"));
        }
        if (!TextUtils.isEmpty(name) && name.contains(List.class.getName()) && !(t instanceof List)) {
            onResponse((T) new ArrayList<>());
        } else {
            onResponse(t);
        }
    }

    @Override
    public void onCancelListener() {
        onFinish();
    }

    /**
     * 显示进度条
     */
    public void showProgress(String message) {
        if (mHub != null) {
            mHub.showProgress(message);
        }
    }

    /**
     * 网络请求成功
     *
     * @param t 成功时返回的参数
     */
    protected abstract void onResponse(T t);

    /**
     * 子类可进行网络请求失败处理
     *
     * @param e 错误，该类继承于 自定义接口类
     */
    protected void onFailure(Throwable e) {

    }

    /**
     * 显示进度条的同时想做什么
     */
    public void preLoad() {

    }

    /**
     * 子类可进行网络请求结束处理，注意，无论是请求失败、成功都会进这个方法
     */
    protected void onFinish() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        //如果设置了AbsRxJavaHelper的isFailResultObject=true,则在这里要将他恢复为false
    }
}
