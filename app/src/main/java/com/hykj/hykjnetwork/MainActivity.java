package com.hykj.hykjnetwork;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.base.network.rxjava.http.ProgressSubscribe;
import com.base.network.rxjava.rec.ResultData;
import com.google.gson.Gson;
import com.hykj.hykjnetwork.http.ApiFactory;
import com.hykj.network.bjzhdj.http.RxJavaHelper;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
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
       /* ObtainCallBack<BaseRec> obtainCallBack = new ObtainCallBack<BaseRec>(this) {//用于测试泛型
            @Override
            public void onFailure(String e) {

            }

            @Override
            public void onResponse(BaseRec rec, BaseRec errorRec) {

            }
        };
        Type t = obtainCallBack.t;
        Type type;
        if (t instanceof ParameterizedType){
            if (((ParameterizedType) t).getActualTypeArguments().length>0){
                type=((ParameterizedType) t).getActualTypeArguments()[0];
            }
        }else if (t instanceof Class){
            type=t;
            String s1 = t.toString();
            String name = ((Class) t).getName();
            if (((Class) t).getName().equals(BaseRec.class.getName())){
                String s = t.toString();
            }
        }
        String s = t.toString();
        String name = BaseRec.class.getName();
        if (s.contains(BaseRec.class.getName())){
            System.out.println(s);
        }*/
        if (false) {
            //单网络请求演示
            Map<String, String> map = new HashMap<>();
            RxJavaHelper.getInstance().toSubscribe(ApiFactory.getInstance().login(map), true, null, ActivityEvent.DESTROY,new ProgressSubscribe<String>(MainActivity.this) {
                @Override
                public void onResponse(String s) {

                }
            });
            //多网络请求演示
            RxJavaHelper.getInstance().zipToSubscribe(ApiFactory.getInstance().login(map), ApiFactory.getInstance().login(map)
                    , true,null, ActivityEvent.DESTROY, new ProgressSubscribe<ResultData<String, String>>(MainActivity.this) {
                        @Override
                        public void onResponse(ResultData<String, String> result) {

                        }
                    });
            //Content-Type=application/json;charset=utf-8请求
            RxJavaHelper.getInstance().toSubscribe(ApiFactory.getInstance().register(RequestBody.create(MediaType.parse("application/json;charset=utf-8")
                    , new Gson().toJson(map))),null, ActivityEvent.DESTROY, new ProgressSubscribe(MainActivity.this) {
                @Override
                public void onResponse(Object o) {

                }
            });
        }
    }
}
