package com.hykj.network.tsw.callback;


import android.support.v4.app.FragmentActivity;

import com.google.gson.Gson;
import com.hykj.network.dialog.ProgressBarDialog;
import com.hykj.network.rxjava.bean.PageInfo;
import com.hykj.network.tsw.rec.BaseRec;

import java.lang.ref.WeakReference;
import java.lang.reflect.ParameterizedType;

public abstract class ObtainCallBack<T extends BaseRec> implements BaseCallBack<T> {
    protected Class<T> t;
    protected WeakReference<FragmentActivity> mWeakAct;
    protected PageInfo pageInfo;
    protected ProgressBarDialog mHub;

    public ObtainCallBack(FragmentActivity activity, PageInfo pageInfo) {
        this.mWeakAct = new WeakReference<>(activity);
        if (activity != null)
            mHub = new ProgressBarDialog().init(activity);
        this.pageInfo = pageInfo;
        init();
    }

    public ObtainCallBack(FragmentActivity activity) {
        this.mWeakAct = new WeakReference<>(activity);
        if (activity != null)
            mHub = new ProgressBarDialog().init(activity);
        init();
    }

    private void init() {
        try {
            ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
            if (type != null && type.getActualTypeArguments().length > 0) {
                t = (Class<T>) type.getActualTypeArguments()[0];
            }
        } catch (Exception e) {
            t = (Class<T>) Object.class;
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

    /**
     * 显示进度条的同时想做什么
     */
    public void preLoad() {
        if (pageInfo != null)
            pageInfo.setLoading(true);
    }

    @Override
    public void onFinish() {
        if (mHub != null) {
            mHub.dismiss();
        }
        if (pageInfo != null)
            pageInfo.setLoading(false);
    }

    @Override
    public T parseNetworkResponse(String result) {
        try {
            return new Gson().fromJson(result, t);
        } catch (Exception e) {
            return null;
        }
    }
}
