package com.hykj.network.yibook.req;


import com.google.gson.Gson;

import com.hykj.network.utils.ReflectUtils;
import com.hykj.network.yibook.callback.ObtainCallBack;
import com.hykj.network.yibook.rec.BaseRec;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * 网络请求基类
 */
public abstract class AbsReq<T extends BaseRec> {
    private String httpUrl;

    public AbsReq(String url) {
        this.httpUrl = url;
    }

    public void doRequest(final ObtainCallBack callBack) {
        doRequest(null, callBack);
    }

    /**
     * @param o        tag
     * @param callBack 回调
     */
    public void doRequest(final Object o, final ObtainCallBack callBack) {
        Map<String, String> params = new LinkedHashMap<>();
        //generalDataProcess(params);
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

        OkHttpUtils.post().url(httpUrl).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                callBack.onFailure(e.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                //这个项目有点怪，当请求的状态值不是0的时候，result返回的就是字符串
                BaseRec errorRec = null;
                T rec = null;
                try {
                    errorRec = new Gson().fromJson(response, BaseRec.class);
                    if (errorRec != null && errorRec.getStatus() == 0) {
                        rec = (T) callBack.parseNetworkResponse(response);
                    }
                } catch (Exception e) {

                }
                callBack.onResponse(o, rec, errorRec);
            }
        });
    }

    protected abstract Map<String, String> addHeaders();


    /**
     * 普通数据处理
     *
     * @param params
     */
    private void generalDataProcess(Map<String, String> params) {
        String json = new Gson().toJson(this);
        json = json.replace("{", "");
        json = json.replace("}", "");
        json = json.replace("\"", "");
        String[] var = json.split(",");
        for (String var1 : var) {
            int index = var1.indexOf(":");
            if (index != -1) {
                String key = var1.substring(0, index);
                String value = var1.substring(index + 1, var1.length());
                if (!key.equals("httpUrl")) {
                    params.put(key, value);
                }
            }
        }
    }
}
