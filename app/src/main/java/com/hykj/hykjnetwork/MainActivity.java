package com.hykj.hykjnetwork;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hykj.network.yibook.req.AbsReq;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AbsReq req = new AbsReq("") {
            @Override
            protected Map<String, String> addHeaders() {
                return null;
            }
        };
    }
}
