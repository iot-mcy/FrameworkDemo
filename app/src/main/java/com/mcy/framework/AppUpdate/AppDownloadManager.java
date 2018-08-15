package com.mcy.framework.AppUpdate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.mcy.framework.text.TestService;
import com.mcy.framework.utils.FileUtils;
import com.mcy.framework.utils.ToastManager;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;

import io.reactivex.functions.Consumer;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mcy.framework.AppUpdate.InstallApk.checkOreoInstallPermission;
import static com.mcy.framework.AppUpdate.InstallApk.installApk;

/**
 * 作者 mcy
 * 日期 2018/8/15 18:05
 * apk下载工具类
 */
public class AppDownloadManager {

    private static String TAG = AppDownloadManager.class.getSimpleName();

    private Context context;

    private static AppDownloadManager manager;

    /**
     * 没有设置的就默认给个名字
     */
    private String fileName = "update.apk";

    public static AppDownloadManager getInstance(Context context) {
        if (manager == null) {
            synchronized (AppDownloadManager.class) {
                if (manager == null) {
                    manager = new AppDownloadManager(context);
                }
            }
        }
        return manager;
    }

    public AppDownloadManager(Context context) {
        this.context = context;
    }

    public AppDownloadManager setFileName(String fileName) {
        // TODO: 2018/8/15 这里已经该是APP的地址
        this.fileName = fileName;
        return this;
    }

    /**
     * 这个先暂时保留
     */
    @SuppressLint("CheckResult")
    public void download() {

        final RxPermissions rxPermissions = new RxPermissions((FragmentActivity) context);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                boolean canInstall = checkOreoInstallPermission(context);
                                if (!canInstall) {
                                    rxPermissions.request(Manifest.permission.REQUEST_INSTALL_PACKAGES)
                                            .subscribe(new Consumer<Boolean>() {
                                                @Override
                                                public void accept(Boolean aBoolean) throws Exception {
                                                    if (aBoolean) {
                                                        // TODO: 2018/8/15 下载
                                                        downloadApk();
                                                    } else {
                                                        // TODO: 2018/8/15 需要调到“安装位置应用的界面”
                                                        context.startActivity(new Intent(context, ActionManageUnknownAPPSourcesActivity.class)
                                                                .putExtra("", fileName));
                                                    }
                                                }
                                            });
                                } else {
                                    // TODO: 2018/8/15 下载
                                    downloadApk();
                                }
                            } else {
                                // TODO: 2018/8/15 下载
                                downloadApk();
                            }

                        } else {
                            Log.i(TAG, "拒绝权限");
                        }
                    }
                });
    }

    /**
     * 正式下载APK
     *
     * @return
     */
    @SuppressLint("CheckResult")
    public void downloadApk() {
        final RxPermissions rxPermissions = new RxPermissions((FragmentActivity) context);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            TestService.downloadApk().enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    ResponseBody responseBody = response.body();
                                    saveFile(responseBody, fileName);
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Log.i(TAG, "下载出错");
                                    ToastManager.showToastShort(context, "下载出错");
                                }
                            });
                        } else {
                            Log.i(TAG, "权限被拒绝");
                        }
                    }
                });
    }

    /**
     * 保存附件
     *
     * @param responseBody
     * @param fileName
     */
    private void saveFile(ResponseBody responseBody, String fileName) {
        File file = FileUtils.createFile(context, fileName);
        if (FileUtils.writeResponseBodyToDisk(responseBody, file)) {
            Log.i(TAG, "文件写入成功");
            installApk(context, file);
        } else {
            Log.i(TAG, "文件写入失败");
            ToastManager.showToastShort(context, "安装失败");
        }
    }
}
