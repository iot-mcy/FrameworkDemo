package com.mcy.framework.AttachmentDownload;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 作者 mcy
 * 日期 2018/8/15 18:05
 * 附件下载工具类
 */
public class AttachmentDownloadManager {

    private static String TAG = AttachmentDownloadManager.class.getSimpleName();

    /**
     * 上下文
     */
    private Context context;

    /**
     *
     */
    private RxPermissions rxPermissions;

    /**
     *
     */
    private static AttachmentDownloadManager manager;

    /**
     * 加载进度框的对话框
     */
    private AlertDialog dialog;

    /**
     * 没有设置的就默认给个名字
     */
    private String fileName = "";

    /**
     * @param context
     * @return
     */
    public static AttachmentDownloadManager getInstance(Context context) {
        if (manager == null) {
            synchronized (AttachmentDownloadManager.class) {
                if (manager == null) {
                    manager = new AttachmentDownloadManager(context);
                }
            }
        }
        return manager;
    }

    /**
     * @param context
     */
    private AttachmentDownloadManager(Context context) {
        this.context = context;
        rxPermissions = new RxPermissions((FragmentActivity) context);
        EventBus.getDefault().register(this);
        initProgressDialog();
    }

    public void clear() {
        context = null;
        manager = null;
        rxPermissions = null;
        fileName = "";
        if (dialog != null) {
            dialog.dismiss();
        }
    }


    /**
     * 文件名称加后缀名
     *
     * @param fileName
     * @return
     */
    public AttachmentDownloadManager setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    /**
     * 正式下载
     *
     * @param path 下载附件的路径
     */
    @SuppressLint("CheckResult")
    public void downloadAttachment(final String path) {
        dialog.show();
        AttachmentDownloadService.downloadAttachment(path, new ProgressListener() {
            @Override
            public void onProgress(long progress, long total, boolean done, int count, int sum) {
                EventBus.getDefault().post(new AttachmentDownloadEntity(progress, total, count, sum, done));
            }
        })
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        saveAttachment(response.body());
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
                .setPositiveButton("后台下载", new DialogInterface.OnClickListener() {
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
//        dialog.show();
    }

    private TextView tvValue;
    private ProgressBar progressBar;

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnEventMsg(AttachmentDownloadEntity entity) {
        int progress = (int) ((entity.getProgress() * 100) / entity.getTotal());
        tvValue.setText(progress + " %");
        progressBar.setProgress(progress);

        if (entity.isDone()) {
            dialog.dismiss();
        }
    }

    /**
     * 保存附件到磁盘
     *
     * @param responseBody
     */
    private void saveAttachment(ResponseBody responseBody) {
        if (responseBody == null) {
            dialog.dismiss();
            ToastManager.showToastShort(context, "下载失败");
            return;
        }
        File file = FileUtils.createFile(context, fileName);
        if (FileUtils.writeResponseBodyToDisk(responseBody, file)) {
            Log.i(TAG, "文件写入成功");
            ToastManager.showToastShort(context, file.getPath() + "\n保存成功");
        } else {
            Log.i(TAG, "文件写入失败");
            ToastManager.showToastShort(context, "保存失败");
        }
    }

    /**
     * 用于通知更新进度条和进度百分比的实体类
     */
    class AttachmentDownloadEntity {
        private long progress;
        private long total;
        private int count;
        private int sum;
        private boolean done;

        public AttachmentDownloadEntity(long progress, long total, int count, int sum, boolean done) {
            this.progress = progress;
            this.total = total;
            this.count = count;
            this.sum = sum;
            this.done = done;
        }

        long getProgress() {
            return progress;
        }

        long getTotal() {
            return total;
        }

        public int getCount() {
            return count;
        }

        public int getSum() {
            return sum;
        }

        public boolean isDone() {
            return done;
        }
    }
}
