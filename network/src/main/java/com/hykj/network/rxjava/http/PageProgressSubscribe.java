package com.hykj.network.rxjava.http;

import android.support.v4.app.FragmentActivity;

import com.hykj.network.rxjava.bean.PageInfo;
import com.hykj.network.rxjava.http.ProgressSubscribe;
import com.hykj.network.zjwy.rec.PageData;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * created by cjf
 * on:2019/4/8 10:23
 * 带分页功能的数据接收
 */
public abstract class PageProgressSubscribe<T> extends ProgressSubscribe<T> {
    protected List<PageInfo> pageInfos = new LinkedList<>();//用于分页结束数据
    //pageInfos个数为1，并且返回数据是ThreeResultData、ResultData、FourResultData、MultiResultData，并且其中含有多个PageData对象时，选择哪一个PageData设置是否有下一页
    protected int singlePage = 0;

    public PageProgressSubscribe(FragmentActivity activity) {
        super(activity);
    }

    public PageProgressSubscribe(FragmentActivity activity, PageInfo pageInfo, int singlePage) {
        super(activity);
        if (pageInfo != null) {
            this.pageInfos.add(pageInfo);
            this.singlePage = singlePage;
        }

    }

    public PageProgressSubscribe(FragmentActivity activity, PageInfo... pageInfos) {
        super(activity);
        if (pageInfos != null)
            this.pageInfos.addAll(Arrays.asList(pageInfos));
    }

    public PageProgressSubscribe(FragmentActivity activity, List<PageInfo> pageInfos) {
        super(activity);
        if (pageInfos != null)
            this.pageInfos.addAll(pageInfos);
    }

    @Override
    public void onNext(T t) {
        super.onNext(t);
        if (pageInfos.size() == 1) {
            if (t instanceof PageData) {//设置是否还有下一页
                pageInfos.get(0).setHasNext(((PageData) t).getTotal());
            } else {
                List<PageData> list = getPageDataList(t);
                if (singlePage < list.size())
                    pageInfos.get(0).setHasNext(list.get(singlePage).getTotal());
            }
        } else if (pageInfos.size() > 1) {
            List<PageData> list = getPageDataList(t);
            if (pageInfos.size() == list.size()) {//只有两者数量对等情况下才设置是否有下一页
                for (int i = 0; i < list.size(); i++) {
                    pageInfos.get(i).setHasNext(list.get(i).getTotal());
                }
            }
        }
    }

    public List<PageData> getPageDataList(T t) {
        List<PageData> list = new ArrayList<>();
        try {
            Field[] fields = t.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object o = field.get(t);
                if (o instanceof PageData)
                    list.add((PageData) o);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        for (PageInfo pageInfo : pageInfos) {//设置加载结束
            pageInfo.setLoading(false);
        }
    }

    @Override
    protected void preLoad() {
        super.preLoad();
        for (PageInfo pageInfo : pageInfos) {//设置加载开始
            pageInfo.setLoading(true);
        }
    }
}
