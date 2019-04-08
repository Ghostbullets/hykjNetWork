package com.hykj.hykjnetwork;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.hykj.hykjnetwork.http.ApiFactory;
import com.hykj.network.bjzhdj.http.ProgressSubscribe;
import com.hykj.network.bjzhdj.http.RxJavaHelper;
import com.hykj.network.bjzhdj.rec.ResultData;
import com.hykj.network.upload.UploadFileReq;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       /* disposable.add(
                new UploadFileReq("", "", "")
                        .addHeader("", "")
                        .addHeaders(null)
                        .doRequest()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                System.out.println("返回的参数是" + s);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e(TAG, throwable.toString());
                            }
                        }));*/
        if (false) {
            //单网络请求演示
            Map<String, String> map = new HashMap<>();
            RxJavaHelper.toSubscribe(ApiFactory.getInstance().login(map), true, new ProgressSubscribe<String>(MainActivity.this) {
                @Override
                protected void onResponse(String s) {

                }
            });
            //多网络请求演示
            RxJavaHelper.zipToSubscribe(ApiFactory.getInstance().login(map), ApiFactory.getInstance().login(map)
                    , true, new ProgressSubscribe<ResultData<String, String>>(MainActivity.this) {
                        @Override
                        protected void onResponse(ResultData<String, String> result) {

                        }
                    });
            //Content-Type=application/json;charset=utf-8请求
            RxJavaHelper.toSubscribe(ApiFactory.getInstance().register(RequestBody.create(MediaType.parse("application/json;charset=utf-8")
                    , new Gson().toJson(map))), new ProgressSubscribe(MainActivity.this) {
                @Override
                protected void onResponse(Object o) {

                }
            });
        }
    }
}
