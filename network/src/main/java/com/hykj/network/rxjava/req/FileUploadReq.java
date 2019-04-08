package com.hykj.network.rxjava.req;

import com.hykj.network.utils.ReflectUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * created by cjf
 * on:2019/3/13 11:11
 * 文件上传请求基类，配合Retrofit使用
 */
public class FileUploadReq<H> extends AbsReq<H> {
    private String name;//文件传入参数名
    private List<File> fileList = new ArrayList<>();//文件列表

    public FileUploadReq(String baseUrl, List<String> filePaths, String name) {
        super(baseUrl);
        this.name = name;
        if (filePaths != null) {
            for (String filePath : filePaths) {
                fileList.add(new File(filePath));
            }
        }
    }

    public FileUploadReq(String baseUrl, String name, String... filePaths) {
        super(baseUrl);
        this.name = name;
        if (filePaths != null) {
            for (String filePath : filePaths) {
                fileList.add(new File(filePath));
            }
        }
    }

    public FileUploadReq(String baseUrl, String name, File... fileList) {
        super(baseUrl);
        this.name = name;
        if (fileList != null)
            this.fileList.addAll(Arrays.asList(fileList));
    }

    public FileUploadReq(String baseUrl, String name, List<File> fileList) {
        super(baseUrl);
        this.name = name;
        if (fileList != null)
            this.fileList.addAll(fileList);
    }

    //得到文件上传RequestBody
    public RequestBody getUploadBody() {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        Map<String, String> params = new LinkedHashMap<>();
        //获取FileUploadReq子类定义的参数,不包括FileUploadReq类定义的全局参数
        ReflectUtils.progressData(params, this, FileUploadReq.class);
        //遍历添加参数
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.addPart(Headers.of("Content-Disposition", "form-data;name=\"" + entry.getKey() + "\""), RequestBody.create(MediaType.parse("text/plain"), entry.getValue()));
        }
        //遍历添加文件
        for (File file : fileList) {
            builder.addPart(Headers.of("Content-Disposition", "form-data;name=\"" + name + "\";fileName=\"" + file.getName() + "\""), RequestBody.create(MediaType.parse("multipart/form-data"), file));
        }
        return builder.setType(MultipartBody.FORM).build();
    }
}
