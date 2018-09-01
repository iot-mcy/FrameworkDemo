package com.mcy.framework.AttachmentDownload;

import com.mcy.framework.service.DownloadServer;
import com.mcy.framework.service.Server;
import com.mcy.framework.utils.ProgressListener;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * 下载附件的服务类
 */
public class AttachmentDownloadService {
    public AttachmentDownloadService() {
    }

    /**
     * @param progressListener
     * @return
     */
    private static AttachmentDownloadServiceInterface serviceInterface(ProgressListener progressListener) {
        synchronized (AttachmentDownloadServiceInterface.class) {
            return DownloadServer.getService(AttachmentDownloadServiceInterface.class, progressListener);
        }
    }

    /**
     * @param path
     * @param progressListener
     * @return
     */
    public static Call<ResponseBody> downloadAttachment(String path, ProgressListener progressListener) {
        return serviceInterface(progressListener).downloadAttachment(path);
    }
}
