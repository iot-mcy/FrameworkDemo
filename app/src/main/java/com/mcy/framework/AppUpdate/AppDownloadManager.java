package com.mcy.framework.AppUpdate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mcy.framework.R;
import com.mcy.framework.utils.FileUtils;
import com.mcy.framework.utils.ProgressListener;
import com.mcy.framework.utils.ToastManager;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

    /**
     * 上下文
     */
    private Context context;

    /**
     *
     */
    private static AppDownloadManager manager;

    /**
     * 加载进度框的对话框
     */
    private AlertDialog dialog;

    /**
     * 没有设置的就默认给个名字
     */
    private String fileName = "update.apk";

    /**
     * @param context
     * @return
     */
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

    /**
     * @param context
     */
    public AppDownloadManager(Context context) {
        this.context = context;
        EventBus.getDefault().register(this);
    }

    /**
     * 另保APP的名字
     *
     * @param fileName
     * @return
     */
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
                                                        downloadApk("");
                                                    } else {
                                                        // TODO: 2018/8/15 需要调到“安装位置应用的界面”
                                                        context.startActivity(new Intent(context, ActionManageUnknownAPPSourcesActivity.class)
                                                                .putExtra("", fileName));
                                                    }
                                                }
                                            });
                                } else {
                                    // TODO: 2018/8/15 下载
                                    downloadApk("");
                                }
                            } else {
                                // TODO: 2018/8/15 下载
                                downloadApk("");
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
     * @param path 下载apk的路径
     */
    @SuppressLint("CheckResult")
    public void downloadApk(final String path) {
        initProgressDialog();
        AppService.downloadApk(path, new ProgressListener() {
            @Override
            public void onProgress(long progress, long total, boolean done, int count, int sum) {

                if (dialog.isShowing()) {
                    EventBus.getDefault().post(new AppDownloadEntity(progress, total, done));
                }
            }
        })
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        saveApk(response.body());
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.i(TAG, "下载出错");
                        ToastManager.showToastShort(context, "下载出错");
                    }
                });
    }

    /**
     * 初始化下载进度框
     */
    private void initProgressDialog() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_attachment_download_progress_layout, null, false);
        tvValue = view.findViewById(R.id.tv_value);
        progressBar = view.findViewById(R.id.progressBar);
        dialog = new AlertDialog.Builder(context)
                .setTitle("正在下载")
                .setIcon(R.drawable.ic_download)
                .setPositiveButton("后台更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        EventBus.getDefault().unregister(this);
                    }
                })
                .setView(view)
                .setCancelable(false)
                .create();
        dialog.show();
    }

    private TextView tvValue;
    private ProgressBar progressBar;

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnEventMsg(AppDownloadEntity entity) {
        int progress = (int) ((entity.getProgress() * 100) / entity.getTotal());
        tvValue.setText(progress + " %");
        progressBar.setProgress(progress);
        if (entity.isDone()) {
            dialog.dismiss();
        }
    }

    /**
     * 保存apk
     *
     * @param responseBody
     */
    private void saveApk(ResponseBody responseBody) {
        if (responseBody == null) {
            dialog.dismiss();
            ToastManager.showToastShort(context, "下载失败");
            return;
        }
        File file = FileUtils.createFile(context, fileName);
        if (FileUtils.writeResponseBodyToDisk(responseBody, file)) {
            Log.i(TAG, "文件写入成功");
            installApk(context, file);
        } else {
            Log.i(TAG, "文件写入失败");
            ToastManager.showToastShort(context, "安装失败");
        }
    }

    /**
     * 用于通知更新进度条和进度百分比的实体类
     */
    class AppDownloadEntity {
        private long progress;
        private long total;
        private boolean done;

        public AppDownloadEntity(long progress, long total, boolean done) {
            this.progress = progress;
            this.total = total;
            this.done = done;
        }

        long getProgress() {
            return progress;
        }

        long getTotal() {
            return total;
        }

        public boolean isDone() {
            return done;
        }
    }
}
