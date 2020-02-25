package com.fuwei.aihospital;

import android.app.Application;

import com.zhouyou.http.EasyHttp;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        EasyHttp.init(this);//默认初始化
    }
}
