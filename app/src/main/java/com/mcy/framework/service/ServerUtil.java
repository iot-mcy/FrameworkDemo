package com.mcy.framework.service;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mcy on 2018/3/23.
 */

public class ServerUtil {

    private static volatile ServerUtil serverUtil;

    public ServerUtil() {
    }

    public static ServerUtil getInstance() {
        if (serverUtil == null) {
            synchronized (ServerUtil.class) {
                if (serverUtil == null) {
                    serverUtil = new ServerUtil();
                }
            }
        }
        return serverUtil;
    }

    public <T> T create(Class<T> tClass) {
        return Server.getInstance().getRetrofit().create(tClass);
    }

    public void requestServer(Observable<String> observable, final JsonCallback callback) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i("", "");
                        disposable = d;
                    }

                    @Override
                    public void onNext(String value) {
                        Log.i("", "");
                        if (!TextUtils.isEmpty(value)) {
                            try {
                                callback.onResponse(JSON.parseObject(value, callback.getType()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            callback.onFailure();
                        }
                        disposable.dispose();//没有注销，onComplete会被调用
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("", "");
                        callback.onFailure();
                        disposable.dispose();
                    }

                    @Override
                    public void onComplete() {
                        Log.i("", "");
                    }
                });
    }


    public Disposable requestServer2(Observable<String> observable, final JsonCallback callback) {
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i("", s);
                        callback.onResponse(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i("", throwable.getMessage());
                        callback.onFailure();
                    }
                });
    }

}
