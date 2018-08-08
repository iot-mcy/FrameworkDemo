package com.mcy.framework.service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.fastjson.FastJsonConverterFactory;

/**
 * 作者 mcy
 * 日期 2018/8/7 17:34
 *
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
                .baseUrl("http://172.16.202.15:8080")//http://localhost:8080/user.svc/get/meng
//                .client(newClientBuilder().build())
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private final long timeOut = 1000 * 30;

    private OkHttpClient.Builder newClientBuilder(){
       return new OkHttpClient.Builder()
                .readTimeout(timeOut, TimeUnit.MILLISECONDS)
                .connectTimeout(timeOut,TimeUnit.MILLISECONDS)
                .writeTimeout(timeOut,TimeUnit.MILLISECONDS)
                .addInterceptor(interceptor);
    }

    private Interceptor interceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request= chain.request();
            Request authorised = request.newBuilder().header("Token","mt-123-456-789").build();
            return chain.proceed(authorised);
        }
    };

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public static <T> T getService(Class<T> tClass) {
        return getInstance().getRetrofit().create(tClass);
    }
}
