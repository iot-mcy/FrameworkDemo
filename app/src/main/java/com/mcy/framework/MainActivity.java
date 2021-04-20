package com.mcy.framework;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.databinding.ObservableField;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
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

import static com.luck.picture.lib.config.PictureMimeType.ofAll;

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

    /**
     * 正式上传多个附件
     *
     * @param localMedia
     */
    private void upLoads(List<LocalMedia> localMedia) {
        List<String> paths = new ArrayList<>();
        for (LocalMedia media : localMedia) {
            paths.add(media.getPath());
        }
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
        if (localMedia.size() > 0) {
            String path = localMedia.get(0).getPath();
            upLoad(path);
        }
    }

    /**
     * 上传多个附件
     *
     * @param view
     */
    public void setOnClickByUploads(View view) {
        if (localMedia.size() > 0) {
            upLoads(localMedia);
        }
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

    /**
     * 这里是已经选好的图片
     */
    List<LocalMedia> localMedia = new ArrayList<>();

    /**
     * 从相册中选图
     *
     * @param view
     */
    public void setOnClickByPhoto(View view) {
        // 进入相册 以下是例子：用不到的api可以不写
        PictureSelector.create(MainActivity.this)
                .openGallery(ofAll())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
//                .theme()//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                .maxSelectNum(9)// 最大图片选择数量 int
                .minSelectNum(1)// 最小选择数量 int
                .imageSpanCount(3)// 每行显示个数 int
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片 true or false
//                .previewVideo(true)// 是否可预览视频 true or false
//                .enablePreviewAudio(true) // 是否可播放音频 true or false
                .isCamera(true)// 是否显示拍照按钮 true or false
                .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
//                .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                .enableCrop(false)// 是否裁剪 true or false
                .compress(false)// 是否压缩 true or false
//                .glideOverride()// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
//                .withAspectRatio()// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
//                .hideBottomControls()// 是否显示uCrop工具栏，默认不显示 true or false
                .isGif(true)// 是否显示gif图片 true or false
//                .compressSavePath(getPath())//压缩图片保存地址
//                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
//                .circleDimmedLayer(false)// 是否圆形裁剪 true or false
//                .showCropFrame()// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
//                .showCropGrid()// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
//                .openClickSound()// 是否开启点击声音 true or false
                .selectionMedia(localMedia)// 是否传入已选图片 List<LocalMedia> list
                .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
//                .cropCompressQuality()// 裁剪压缩质量 默认90 int
//                .minimumCompressSize(100)// 小于100kb的图片不压缩
//                .synOrAsy(true)//同步true或异步false 压缩 默认同步
//                .cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
//                .rotateEnabled() // 裁剪是否可旋转图片 true or false
//                .scaleEnabled()// 裁剪是否可放大缩小图片 true or false
//                .videoQuality()// 视频录制质量 0 or 1 int
//                .videoMaxSecond(15)// 显示多少秒以内的视频or音频也可适用 int
//                .videoMinSecond(10)// 显示多少秒以内的视频or音频也可适用 int
//                .recordVideoSecond()//视频秒数录制 默认60s int
//                .isDragFrame(false)// 是否可拖动裁剪框(固定)
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PictureConfig.CHOOSE_REQUEST:
                // 图片、视频、音频选择结果回调
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                localMedia = selectList;
                if (localMedia != null && localMedia.size() > 0) {
                    Glide.with(this).load(localMedia.get(0).getPath()).into(binding.imageView);
                }
                // 例如 LocalMedia 里面返回三种path
                // 1.media.getPath(); 为原图path
                // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
//                    adapter.setList(selectList);
//                    adapter.notifyDataSetChanged();
                break;
            default:
                break;
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
