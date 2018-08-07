package com.mcy.framework.service;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.fastjson.FastJsonConverterFactory;

/**
 * Created by mcy on 2018/3/22.
 */

public class Server {

    private volatile static Server sInstance;

    private Retrofit retrofit;

    public static Server getInstance() {
        if (sInstance == null) {
            synchronized (Server.class) {
                if (sInstance == null) {
                    sInstance = new Server();
                }
            }
        }
        return sInstance;
    }

    private Server() {
        retrofit = newRetrofit();
    }

    protected Retrofit newRetrofit() {
        return new Retrofit.Builder()
                .baseUrl("http://172.16.200.192:6621")
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
