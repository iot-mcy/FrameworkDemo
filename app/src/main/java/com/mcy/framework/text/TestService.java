package com.mcy.framework.text;

import com.alibaba.fastjson.JSONObject;
import com.mcy.framework.baseEntity.ResponseEntity;
import com.mcy.framework.service.Server;
import com.mcy.framework.user.User;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;

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

    public static Single<String> getPublishActivityList(JSONObject object) {
        return textService()
                .getPublishActivityList(object);
    }

    public static Flowable<TradeQuotedPrice> getData2() {
        JSONObject object = new JSONObject();
        object.put("ID", 1000);

        return textService()
                .getTradeQuotedPriceList2(object);
    }

    public static Flowable<ResponseEntity<User>> login(String username, String password) {
        JSONObject object = new JSONObject();
        object.put("username", username);
        object.put("password", password);
        return textService().login(object);
    }

    public static Flowable<String> logout() {
        return textService().logout();
    }

    public static Flowable<String> getUserByUserID(int name) {
        return textService().getUserByUserID(name);
    }
}
