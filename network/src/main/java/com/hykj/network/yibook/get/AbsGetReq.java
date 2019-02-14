package com.hykj.network.yibook.get;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.hykj.network.utils.ReflectUtils;
import com.hykj.network.utils.Utils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Util;

/**
 * get请求
 *
 * @param <T>
 */
public class AbsGetReq<T extends BaseGetRec> {
    private String httpUrl;
    private Map<String, String> params = new LinkedHashMap<>();
    public Handler mHandler = new Handler(Looper.getMainLooper());

    public AbsGetReq(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    public void doRequest(ObtainGetCallBack callBack) {
        doRequest(null, callBack);
    }

    public void doRequest(final Object o, final ObtainGetCallBack callBack) {
        ReflectUtils.progressData(params, this, AbsGetReq.class);
        if (params.size() > 0 && !"?".equals(httpUrl.substring(httpUrl.length() - 1)))
            httpUrl = httpUrl + "?";
        StringBuilder builder = new StringBuilder(httpUrl);
        int i = 1;
        for (Map.Entry entry : params.entrySet()) {
            builder.append(entry.getKey());
            builder.append("=");
            builder.append(entry.getValue());
            if (params.size() > i) {
                builder.append("&");
                i++;
            }
        }
        Request request = new Request.Builder().get().url(builder.toString()).build();
        new OkHttpClient.Builder()
                .build()
                .newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onFailure(e.toString());
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        try {
                            final T rec = (T) callBack.parseNetworkResponse(response.body().string());
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callBack.onResponse(o, rec);
                                }
                            });
                        } catch (Exception e) {

                        }
                    }
                });
    }

    public void addParams(String key, String value) {
        Utils.checkNameAndValue(key, value);
        this.params.put(key, value);
    }

    public void addParamsSet(Map<String, String> params) {
        if (params == null) throw new NullPointerException("params == null");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            Utils.checkNameAndValue(key, value);
            this.params.put(key, value);
        }
    }
}
