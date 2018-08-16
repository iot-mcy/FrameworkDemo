package com.mcy.framework.AttachmentUpload;

import com.alibaba.fastjson.JSON;
import com.mcy.framework.service.Server;
import com.mcy.framework.utils.ProgressListener;
import com.mcy.framework.utils.ProgressRequestBody;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 上传附件的服务类
 */
public class AttachmentUploadService {
    public AttachmentUploadService() {
    }

    /**
     * @return
     */
    private static AttachmentUploadServiceInterface serviceInterface() {
        synchronized (AttachmentUploadServiceInterface.class) {
            return Server.getService(AttachmentUploadServiceInterface.class);
        }
    }

    /**
     * 上传单个附件
     *
     * @param file
     * @param progressListener
     * @return
     */
    public static Observable<String> uploadAttachment(File file, ProgressListener progressListener) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        ProgressRequestBody progressRequestBody = new ProgressRequestBody(requestFile, progressListener);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), progressRequestBody);
        return serviceInterface().uploadAttachment(part);
    }

    /**
     * 上传多个附件
     *
     * @param paths            需要上传的附件集合
     * @param progressListener 上传监听回调
     * @return
     */
    public static Observable<String> uploadAttachments(List<String> paths, ProgressListener progressListener) {
        List<MultipartBody.Part> parts = new ArrayList<>();
        int sum = paths.size();
        int count = 1;
        for (String filePath : paths) {
            File file = new File(filePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            ProgressRequestBody progressRequestBody = new ProgressRequestBody(requestFile, progressListener, count, sum);
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), progressRequestBody);
            parts.add(part);
            count++;
        }
        String json = JSON.toJSONString(paths);
        return serviceInterface().uploadAttachments(parts);
    }
}
