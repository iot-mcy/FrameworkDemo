package com.mcy.framework.text;

import com.alibaba.fastjson.JSONObject;
import com.mcy.framework.service.Server;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;

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

    public static Observable<String> uploadMemberIcon(MultipartBody.Part part) {
        return textService().uploadMemberIcon(part);
    }

    public static Observable<String> uploadAttachments(List<MultipartBody.Part> parts) {
        return textService().uploadAttachments(parts);
    }

    public static Observable<ResponseBody> downloadAttaachments(String fileName) {
        return textService().download(fileName);
    }

    public static Observable<ResponseBody> downloadApk() {
        return textService().downloadApk();
    }
}
