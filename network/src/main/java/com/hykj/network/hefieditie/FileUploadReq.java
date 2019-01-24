package com.hykj.network.hefieditie;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.hykj.network.hefieditie.callback.CallBack;
import com.hykj.network.utils.ReflectUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 文件上传封装
 */
public class FileUploadReq {
    private String uploadUrl;//上传url地址
    private String fileName;//文件传入参数名
    private List<File> fileList = new ArrayList<>();//文件列表
    private Map<String, String> headers = new LinkedHashMap<>();//头部参数
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public FileUploadReq(List<String> filePaths, String uploadUrl, String fileName) {
        this.uploadUrl = uploadUrl;
        this.fileName = fileName;
        if (filePaths != null) {
            for (String filePath : filePaths) {
                fileList.add(new File(filePath));
            }
        }
    }

    public FileUploadReq(String uploadUrl, String fileName, String... filePaths) {
        this.uploadUrl = uploadUrl;
        this.fileName = fileName;
        if (filePaths != null) {
            for (String filePath : filePaths) {
                fileList.add(new File(filePath));
            }
        }
    }

    public FileUploadReq(String uploadUrl, String fileName, File... fileList) {
        this.uploadUrl = uploadUrl;
        this.fileName = fileName;
        if (fileList != null)
            this.fileList.addAll(Arrays.asList(fileList));
    }

    public FileUploadReq(String uploadUrl, String fileName, List<File> fileList) {
        this.uploadUrl = uploadUrl;
        this.fileName = fileName;
        if (fileList != null)
            this.fileList.addAll(fileList);
    }

    public void doRequest(CallBack callBack) {
        doRequest(null, callBack);
    }

    public void doRequest(final Object o, final CallBack callBack) {
        Map<String, String> params = new LinkedHashMap<>();
        //获取FileUploadReq子类定义的参数
        ReflectUtils.progressData(params, this, FileUploadReq.class);
        //定义一个多文件、多参数内容的主体
        MultipartBody.Builder builder = new MultipartBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            //遍历添加参数
            builder.addPart(Headers.of("Content-Disposition", "form-data;name=\"" + entry.getKey() + "\""), RequestBody.create(null, entry.getValue()));
            //上下两句等同，下面这句内部做了封装
            //builder.addFormDataPart(entry.getKey(),entry.getValue());
        }
        //遍历添加文件数据
        for (File file : fileList) {
            builder.addPart(Headers.of("Content-Disposition", "form-data;name=\"" + fileName + "\";filename=\"" + file.getName() + "\""), RequestBody.create(MediaType.parse("application/octet-stream"), file));
        }
        //得到一个包装好的主体
        MultipartBody body = builder.setType(MultipartBody.FORM).build();
        Request.Builder build = new Request.Builder();
        //设置头部
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            build.addHeader(entry.getKey(), entry.getValue());
        }
        //创建一个请求
        Request request = build.post(body).url(uploadUrl).build();
        //异步网络请求
        new OkHttpClient.Builder().build().newCall(request).enqueue(new Callback() {
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
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                final Object rec = callBack.parseNetworkResponse(json);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onResponse(o, rec);
                    }
                });
            }
        });
    }

    /**
     * 添加头部参数
     *
     * @param headers 参数集
     */
    public void addHeaders(Map<String, String> headers) {
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                this.headers.put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 添加单个头部参数
     *
     * @param key   键
     * @param value 值
     */
    public void addHeader(String key, String value) {
        if (!TextUtils.isEmpty(key)) {
            this.headers.put(key, value);
        }
    }
}
