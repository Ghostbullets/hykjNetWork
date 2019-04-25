package com.hykj.network.tsw.req;


import com.google.gson.Gson;
import com.hykj.network.tsw.callback.ObtainCallBack;
import com.hykj.network.tsw.rec.BaseRec;
import com.hykj.network.utils.ReflectUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * 网络请求基类
 */
public abstract class AbsReq {
    private String httpUrl;

    public AbsReq(String url) {
        this.httpUrl = url;
    }

    public void doRequest(ObtainCallBack callBack) {
        doRequest(true, null, callBack);
    }

    public void doRequest(boolean showProgress, ObtainCallBack callBack) {
        doRequest(showProgress, null, callBack);
    }

    /**
     * @param showProgress 是否显示弹窗
     * @param progress     弹窗字符串
     * @param callBack     回调
     */
    public void doRequest(boolean showProgress, String progress, final ObtainCallBack callBack) {
        if (showProgress) {
            callBack.showProgress(progress);
        }
        callBack.preLoad();

        Map<String, String> params = new LinkedHashMap<>();
        ReflectUtils.progressData(params, this, AbsReq.class);

        Map<String, String> headers = addHeaders();
        if (headers != null) {
            Iterator<Map.Entry<String, String>> iterator = headers.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> next = iterator.next();
                String key = next.getKey();
                String value = next.getValue();
                if (value == null)
                    continue;
                params.put(key, value);
            }
        }
        String content = encrypt(new Gson().toJson(params));
        OkHttpUtils.post().url(httpUrl).addParams("content", content).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                callBack.onFailure(e.toString());
                callBack.onFinish();
            }

            @Override
            public void onResponse(String response, int id) {
                //这个项目有点怪，当请求的状态值不是0的时候，result返回的就是字符串
                response = decrypt(response);
                BaseRec errorRec = null;
                Object rec = null;
                try {
                    errorRec = new Gson().fromJson(response, BaseRec.class);
                    if (errorRec != null && errorRec.getStatus() == 0) {
                        rec = callBack.parseNetworkResponse(response);
                    }
                } catch (Exception e) {

                }
                callBack.onResponse(rec, errorRec);
                callBack.onFinish();
            }
        });
    }

    protected abstract Map<String, String> addHeaders();

    protected String encrypt(String encrypt) {
        return encrypt;
    }

    protected String decrypt(String decrypt) {
        return decrypt;
    }
}
