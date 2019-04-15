package com.hykj.network.rxjava.port;

import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

/**
 * created by cjf
 * on:2019/4/15 9:22
 * obj 在Activity页面调用时请传入{@link ActivityEvent}，在Fragment碎片调用时请传入{@link FragmentEvent}
 */
public class RxImplView {
    private RxView rxView;
    private Object obj;

    public RxImplView(RxView rxView, Object obj) {
        this.rxView = rxView;
        this.obj = obj;
    }

    public <T> LifecycleTransformer<T> bindToUntilEvent() {
        return rxView.bindToUntilEvent(obj);
    }
}
