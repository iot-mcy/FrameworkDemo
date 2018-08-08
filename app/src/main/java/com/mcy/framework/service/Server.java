package com.mcy.framework.service;

import com.mcy.framework.BuildConfig;

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
 */
public class Server {

    private volatile static Server sInstance;

    private transient Retrofit retrofit;

    private static Server getInstance() {
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

    private Retrofit newRetrofit() {
        String baseUrl = "http://" + BuildConfig.IP + ":" + BuildConfig.PORT;
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(newClientBuilder().build())
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private OkHttpClient.Builder newClientBuilder() {
        long timeOut = 1000 * 30;
        return new OkHttpClient.Builder()
                .readTimeout(timeOut, TimeUnit.MILLISECONDS)
                .connectTimeout(timeOut, TimeUnit.MILLISECONDS)
                .writeTimeout(timeOut, TimeUnit.MILLISECONDS)
                .addInterceptor(interceptor);
    }

    private Interceptor interceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Request authorised = request.newBuilder().header("Token", "mt-123-456-789").build();
            return chain.proceed(authorised);
        }
    };

    private Retrofit getRetrofit() {
        return retrofit;
    }

    public static <T> T getService(Class<T> tClass) {
        return getInstance().getRetrofit().create(tClass);
    }
}
