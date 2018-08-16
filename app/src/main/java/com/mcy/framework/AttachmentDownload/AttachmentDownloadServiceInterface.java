package com.mcy.framework.AttachmentDownload;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * 下载附件的接口
 */
public interface AttachmentDownloadServiceInterface {
    @POST("/user.svc/downloadAttachment/{filename}")
    Call<ResponseBody> downloadAttachment(@Path("filename") String filename);
}
