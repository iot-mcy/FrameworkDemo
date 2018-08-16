package com.mcy.framework.text;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.luck.picture.lib.entity.LocalMedia;
import com.mcy.framework.service.Server;
import com.mcy.framework.utils.ProgressListener;
import com.mcy.framework.utils.ProgressRequestBody;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * 作者 mcy
 * 日期 2018/8/7 17:32
 * 测试用
 */
public class TestService {
    public TestService() {
    }

    private static TextServiceInterface textService() {
        synchronized (TextServiceInterface.class) {
            return Server.getService(TextServiceInterface.class);
        }
    }


    public static Observable<GetTradeQuotedPriceByID> getData1() {
        JSONObject object = new JSONObject();
        object.put("ID", 1000);

        return textService()
                .getTradeQuotedPriceList(object);
    }

    public static Flowable<GetTradeQuotedPriceByID> getData2() {
        JSONObject object = new JSONObject();
        object.put("ID", 1000);

        return textService()
                .getTradeQuotedPriceList2(object);
    }

    public static Flowable<String> login(String username, String password) {
        JSONObject object = new JSONObject();
        object.put("username", username);
        object.put("password", password);
        return textService().login(object);
    }

    public static Flowable<String> getUserByUserID(int name) {
        return textService().getUserByUserID(name);
    }

    public static Observable<String> uploadMemberAttachment(File file, ProgressListener progressListener) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        ProgressRequestBody progressRequestBody = new ProgressRequestBody(requestFile, progressListener);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), progressRequestBody);
        return textService().uploadAttachment(part);
    }

    public static Observable<String> uploadAttachments(List<LocalMedia> localMedia, ProgressListener progressListener) {
        List<MultipartBody.Part> parts = new ArrayList<>();
        int sum = localMedia.size();
        int count = 0;
        for (LocalMedia filePath : localMedia) {
            count++;
            File file = new File(filePath.getPath());
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            ProgressRequestBody progressRequestBody = new ProgressRequestBody(requestFile, progressListener, count, sum);
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), progressRequestBody);
            parts.add(part);
        }
        String json = JSON.toJSONString(localMedia);
        return textService().uploadAttachments(parts);
    }

    public static Observable<ResponseBody> downloadAttaachments(String fileName) {
        return textService().download(fileName);
    }

    public static Call<ResponseBody> downloadApk(String path) {
        return textService().downloadApk(path);
    }
}
