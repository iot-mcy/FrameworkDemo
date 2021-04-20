package com.mcy.framework;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.ObservableField;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mcy.framework.AppUpdate.AppDownloadManager;
import com.mcy.framework.AttachmentDownload.AttachmentDownloadManager;
import com.mcy.framework.AttachmentUpload.AttachmentUploadManager;
import com.mcy.framework.AttachmentUpload.UploadListener;
import com.mcy.framework.base.BaseActivity;
import com.mcy.framework.baseEntity.ResponseEntity;
import com.mcy.framework.databinding.ActivityMainBinding;
import com.mcy.framework.temp.ITempView;
import com.mcy.framework.text.TestService;
import com.mcy.framework.text.TradeQuotedPrice;
import com.mcy.framework.ui.TestLoadingActivity;
import com.mcy.framework.user.User;
import com.mcy.framework.utils.DownloadProgress;
import com.mcy.framework.utils.FileUtils;
import com.mcy.framework.utils.ToastManager;
import com.mcy.framework.utils.UploadProgress;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    public ObservableField<String> data = new ObservableField<>("hello world!");

    private ActivityMainBinding binding;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testLoading();
    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        binding = (ActivityMainBinding) viewDataBinding;
        binding.setActivity(this);

        binding.progressBar.setMax(100);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        getData1();
//        getData2();
//        getUserByUsername(1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
        AttachmentDownloadManager.getInstance(this).clear();
    }

    @Override
    public void onBtTempClick(View view, int type) {
        super.onBtTempClick(view, type);
        testLoading();
    }

    private void testLoading() {
        showTempView(ITempView.LOADING);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hintTempView();
            }
        }, 1000);
    }

    public void clearAllRequest(View view) {
        compositeDisposable.dispose();
    }

    public void getPublishActivityList(View view) {
        JSONObject object = new JSONObject();
        object.put("CropID", 4005);
        object.put("PhoneDeviceID", "");
        compositeDisposable.add(TestService.getPublishActivityList(object)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
                        if (!TextUtils.isEmpty(response)) {
                            Log.d(TAG, response);
                            data.set(response);
                        } else {
                            Toast.makeText(MainActivity.this, "失败\n", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, "失败\n" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }));
    }

    private void getData2() {
        compositeDisposable.add(TestService.getData2()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TradeQuotedPrice>() {
                    @Override
                    public void accept(TradeQuotedPrice getTradeQuotedPriceByID) throws Exception {
                        if (getTradeQuotedPriceByID != null) {
                            String json = JSON.toJSONString(getTradeQuotedPriceByID);
                            Toast.makeText(MainActivity.this, "成功\n" + json, Toast.LENGTH_LONG).show();
                            data.set(json);
                        } else {
                            Toast.makeText(MainActivity.this, "失败\n", Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, "失败\n" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }));
    }

    private void getUserByUsername(int userID) {
        compositeDisposable.add(TestService.getUserByUserID(userID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Toast.makeText(MainActivity.this, "成功\n" + s, Toast.LENGTH_LONG).show();
                        data.set(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, "失败\n" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }));
    }

    public void setOnClickByError(View view) {
        showTempView(ITempView.ERROR);
    }

    public void setOnClickByDataNull(View view) {
        showTempView(ITempView.DATA_NULL);
    }

    public void setOnClickByLoading(View view) {
        showTempView(ITempView.LOADING);
    }

    public void setOnClickByTestLoading(View view) {
        startActivity(new Intent(this, TestLoadingActivity.class));
    }


    /**
     * 登录
     *
     * @param view
     */
    public void setOnClickByLogin(View view) {
        compositeDisposable.add(TestService.login("mcy", "123456")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseEntity<User>>() {
                    @Override
                    public void accept(ResponseEntity<User> userResponseEntity) {
                        Log.i("", "");
                        data.set(JSON.toJSONString(userResponseEntity));
                        User.updateCurrentUser(userResponseEntity.getData());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.i("", "");
                        data.set(throwable.getMessage());
                    }
                }));
    }

    /**
     * 登录
     *
     * @param view
     */
    public void setOnClickByLogout(View view) {
        compositeDisposable.add(TestService.logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i("", "");
                        data.set(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i("", "");
                        data.set(throwable.getMessage());
                    }
                }));
    }

    /**
     * 清除log
     *
     * @param view
     */
    public void setOnClickByLog(View view) {
        data.set("");
        binding.progressBar.setProgress(0);
        binding.btDownload.setText("下载附件");
    }

    /**
     * 正式上传单张图片的地方
     *
     * @param filePath
     */
    private void upLoad(String filePath) {
        // TODO: 2018/9/1
    }

    public void setOnClickByPhoto(View view) {

    }

    /**
     * 正式上传多个附件
     *
     * @param paths
     */
    private void upLoads(List<String> paths) {
        AttachmentUploadManager
                .getInstance(this)
                .uploadAttachments(paths, new UploadListener() {
                    @Override
                    public void done(String data) {
                        ToastManager.showToastShort(MainActivity.this, data);
                    }

                    @Override
                    public void error(Throwable throwable) {
                        ToastManager.showToastShort(MainActivity.this, throwable.getMessage());
                    }
                });
    }

    /**
     * 上传单张图片
     *
     * @param view
     */
    public void setOnClickByUpload(View view) {

    }

    /**
     * 上传多个附件
     *
     * @param view
     */
    public void setOnClickByUploads(View view) {

    }

    /**
     * 下载
     *
     * @param view
     */
    @SuppressLint("CheckResult")
    @TargetApi(Build.VERSION_CODES.M)
    public void setOnClickByDownload(View view) {
//        downloadApk("a.apk");
        downloadAttachment("图1.jpg");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnEventMsg(DownloadProgress downloadProgress) {
        int f = (int) ((downloadProgress.getCurrentLength() * 100) / downloadProgress.getTotalLength());
        Log.d("DownloadProgress", f + "%");
        binding.btDownload.setText(f + "%");
        binding.progressBar.setProgress(f);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnEventMsg(UploadProgress uploadProgress) {
        int f = (int) ((uploadProgress.getCurrentLength() * 100) / uploadProgress.getTotalLength());
        Log.d("uploadProgress", f + "%");
//        binding.btDownload.setText(f + "%" + " , " + uploadProgress.getCount() + "/" + uploadProgress.getSum());
//        binding.progressBar.setProgress(f);
    }

    @SuppressLint("CheckResult")
    private void downloadApk(final String fileName) {
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            String path = "/pc6_soure/2018-5/com.frego.flashlight_1286.apk";
                            AppDownloadManager.getInstance(MainActivity.this)
                                    .setFileName(fileName)
                                    .downloadApk(path);
                        }
                    }
                });

    }

    @SuppressLint("CheckResult")
    private void downloadAttachment(final String fileName) {
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            String path = "IMG_20180728_103505.jpg";
                            AttachmentDownloadManager.getInstance(MainActivity.this)
                                    .setFileName(fileName)
                                    .downloadAttachment(path);
                        }
                    }
                });

    }

    private File file;

    /**
     * 保存附件
     *
     * @param responseBody
     * @param fileName
     */
    private void saveFile(ResponseBody responseBody, String fileName) {
        file = FileUtils.createFile(MainActivity.this, fileName);
        if (FileUtils.writeResponseBodyToDisk(responseBody, file)) {
            Log.i("", "");
            data.set("成功");

        } else {
            Log.i("", "");//可以尝试2次写入
            data.set("失败");
        }
    }

    private Disposable disposable;

    public void testRxJava1(View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "Observable thread is : " + Thread.currentThread().getName());
                emitter.onNext(1);
//                emitter.onNext(2);
//                emitter.onNext(3);
//                emitter.onComplete();
//                emitter.onNext(4);
            }
        }).subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "(mainThread) thread is : " + Thread.currentThread().getName());
                        Log.d(TAG, "accept：" + integer);
                    }
                })
                .observeOn(Schedulers.io())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe");
                        disposable = d;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "Observable thread is : " + Thread.currentThread().getName());
                        Log.d(TAG, "onNext：" + integer);
                        if (integer == 2) {
                            Log.d(TAG, "dispose");
                            disposable.dispose();
                            Log.d(TAG, "isDisposed：" + disposable.isDisposed());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    @SuppressLint("CheckResult")
    public void testRxJava2(View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }
        }).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return "改变后：" + integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.d(TAG, throwable.getMessage());
            }
        });
    }

    @SuppressLint("CheckResult")
    public void testRxJava3(View view) {
        List<String> list = new ArrayList<String>() {{
            add("A");
            add("B");
        }};
        Observable.just(list)
                .flatMap(new Function<List<String>, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(List<String> list) throws Exception {
                        List<String> strings = new ArrayList<>();
                        for (String str : list) {
                            strings.add(str + "0");
                        }
                        return Observable.fromIterable(strings);
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(TAG, s);
                    }
                });

        Observable.just(list).map(new Function<List<String>, String>() {
            @Override
            public String apply(List<String> list) throws Exception {
                String json = JSON.toJSONString(list);
                return json;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        });

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }
        }).flatMap(new Function<Integer, ObservableSource<String>>() {

            @Override
            public ObservableSource<String> apply(final Integer integer) throws Exception {
                List<String> list = new ArrayList<String>() {{
                    add("A" + integer);
                    add("B" + integer);
                }};
                return Observable.fromIterable(list).delay(1, TimeUnit.SECONDS);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.d(TAG, throwable.getMessage());
            }
        });
    }

    @SuppressLint("CheckResult")
    public void testRxJava4(View view) {
        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "emitter 1");
                emitter.onNext(1);
                Log.d(TAG, "emitter 2");
                emitter.onNext(2);
                Log.d(TAG, "emitter 3");
                emitter.onNext(3);
                Log.d(TAG, "emitter 4");
                emitter.onNext(4);
                Log.d(TAG, "emitter onComplete1");
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Log.d(TAG, "emitter A");
                emitter.onNext("A");
                Log.d(TAG, "emitter B");
                emitter.onNext("B");
                Log.d(TAG, "emitter C");
                emitter.onNext("C");
                Log.d(TAG, "emitter onComplete2");
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(Integer integer, String s) throws Exception {
                return integer + s;
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, s);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private Subscription subscription;

    @SuppressLint("CheckResult")
    public void testRxJava5(View view) {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "emitter 1");
                emitter.onNext(1);
                Thread.sleep(1000);
                Log.d(TAG, "emitter 2");
                emitter.onNext(2);
                Thread.sleep(1000);
                Log.d(TAG, "emitter 3");
                emitter.onNext(3);
                Thread.sleep(1000);
                Log.d(TAG, "emitter onComplete");
                emitter.onComplete();
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        subscription = s;
                        subscription.request(1);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext " + integer);
                        subscription.request(1);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.d(TAG, "onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    public void setOnClickByRxJavaFlowable(View view) {
        if (subscription != null) {
            subscription.request(1);
        }
    }
}
