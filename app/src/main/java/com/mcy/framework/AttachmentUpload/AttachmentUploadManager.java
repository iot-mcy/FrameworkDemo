package com.mcy.framework.AttachmentUpload;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mcy.framework.R;
import com.mcy.framework.utils.ProgressListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 作者 mcy
 * 日期 2018/8/15 18:05
 * 附件下载工具类
 */
public class AttachmentUploadManager {

    private static String TAG = AttachmentUploadManager.class.getSimpleName();

    /**
     * 上下文
     */
    private Context context;

    /**
     *
     */
    private static AttachmentUploadManager manager;

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
    public static AttachmentUploadManager getInstance(Context context) {
        if (manager == null) {
            synchronized (AttachmentUploadManager.class) {
                if (manager == null) {
                    manager = new AttachmentUploadManager(context);
                }
            }
        }
        return manager;
    }

    /**
     * @param context
     */
    public AttachmentUploadManager(Context context) {
        this.context = context;
        EventBus.getDefault().register(this);
    }

    /**
     * 正式上传
     *
     * @param path 下载附件的路径
     */
    @SuppressLint("CheckResult")
    public void uploadAttachments(final List<String> path, final UploadListener uploadListener) {
        initProgressDialog();
        AttachmentUploadService.uploadAttachments(path, new ProgressListener() {
            @Override
            public void onProgress(long progress, long total, boolean done, int count, int sum) {

                if (dialog.isShowing()) {
                    EventBus.getDefault().post(new AttachmentUploadEntity(progress, total, count, sum, done));
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(TAG, "上传成功");
                        uploadListener.done(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i(TAG, "上传出错");
                        uploadListener.error(throwable);
                    }
                });
    }

    /**
     * 初始化上传进度框
     */
    private void initProgressDialog() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_attachment_upload_progress_layout, null, false);
        tvValue = view.findViewById(R.id.tv_value);
        tvUploadProgress = view.findViewById(R.id.tv_upload_progress);
        progressBar = view.findViewById(R.id.progressBar);
        dialog = new AlertDialog.Builder(context)
                .setTitle("正在上传")
                .setIcon(R.drawable.ic_upload)
                .setPositiveButton("后台上传", new DialogInterface.OnClickListener() {
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

    private TextView tvValue, tvUploadProgress;
    private ProgressBar progressBar;

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnEventMsg(AttachmentUploadEntity entity) {
        int progress = (int) ((entity.getProgress() * 100) / entity.getTotal());
        tvValue.setText(progress + " %");
        progressBar.setProgress(progress);
        tvUploadProgress.setText("上传进度：" + entity.count + "/" + entity.getSum());

        if (entity.isDone() && entity.getCount() == entity.getSum()) {
            dialog.dismiss();
        }
    }

    /**
     * 用于通知更新进度条和进度百分比的实体类
     */
    class AttachmentUploadEntity {
        private long progress;
        private long total;
        private int count;
        private int sum;
        private boolean done;

        public AttachmentUploadEntity(long progress, long total, int count, int sum, boolean done) {
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

    /**
     * Toast提示同时实体类
     */
    class ToastMsg {
        private String msg;

        public ToastMsg(String msg) {
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }
    }
}
