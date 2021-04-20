package com.mcy.framework.service;

import com.mcy.framework.BuildConfig;
import com.mcy.framework.user.User;

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
 * 请求服务
 *
 * @author mcy
 */
public class Server {

    /**
     *
     */
    private volatile static Server sInstance;

    /**
     *
     */
    private transient Retrofit retrofit;

    /**
     * @return
     */
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

    /**
     *
     */
    private Server() {
        retrofit = newRetrofit();
    }

    /**
     * @return
     */
    private Retrofit newRetrofit() {
        String baseUrl = "http://" + BuildConfig.IP + ":" + BuildConfig.PORT;
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(newClientBuilder().build())
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    /**
     * @return
     */
    private OkHttpClient.Builder newClientBuilder() {
        //60秒
        long timeOut = 60;
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(timeOut, TimeUnit.SECONDS)
                .connectTimeout(timeOut, TimeUnit.SECONDS)
                .writeTimeout(timeOut, TimeUnit.SECONDS)
                .addInterceptor(interceptor);
        return builder;
    }

    /**
     * 目的添加token
     */
    private Interceptor interceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Request authorised = request.newBuilder().header("Token", User.getInstance().getToken()).build();
            return chain.proceed(authorised);
        }
    };

    /**
     * @return
     */
    private Retrofit getRetrofit() {
        return retrofit;
    }

    /**
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T getService(Class<T> tClass) {
        return getInstance().getRetrofit().create(tClass);
    }
}
