package com.hykj.network.xxgj.callback;


import android.support.v4.app.FragmentActivity;

import com.google.gson.Gson;
import com.hykj.network.dialog.ProgressBarDialog;
import com.hykj.network.rxjava.bean.PageInfo;
import com.hykj.network.xxgj.rec.BaseRec;

import java.lang.ref.WeakReference;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class ObtainCallBack<T extends BaseRec> implements BaseCallBack<T> {
    private Type t;
    protected PageInfo pageInfo;
    protected ProgressBarDialog mHub;

    public ObtainCallBack(FragmentActivity activity, PageInfo pageInfo, Type t) {
        init(activity, pageInfo, t);
    }

    public ObtainCallBack(FragmentActivity activity, PageInfo pageInfo) {
        init(activity, pageInfo, null);
    }

    public ObtainCallBack(FragmentActivity activity, Type t) {
        init(activity, null, t);
    }

    public ObtainCallBack(FragmentActivity activity) {
        init(activity, null, null);
    }

    public ObtainCallBack(Class<T> t) {
        init(null, null, t);
    }

    public ObtainCallBack() {
        init(null, null, null);
    }

    private void init(FragmentActivity activity, PageInfo pageInfo, Type t) {
        this.pageInfo = pageInfo;
        this.t = t;
        if (activity != null)
            mHub = new ProgressBarDialog().init(activity);
        if (this.t == null) {
            try {
                ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
                if (type != null && type.getActualTypeArguments().length > 0) {
                    this.t = type.getActualTypeArguments()[0];
                }
            } catch (ClassCastException e) {
                this.t = BaseRec.class;
            }
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
