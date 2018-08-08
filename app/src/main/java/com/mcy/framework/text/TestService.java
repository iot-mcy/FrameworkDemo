package com.mcy.framework.text;

import com.alibaba.fastjson.JSONObject;
import com.mcy.framework.service.Server;

import io.reactivex.Flowable;

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


    public static Flowable<String> getData1() {
        JSONObject object = new JSONObject();
        object.put("ID", 1000);

        return textService()
                .getTradeQuotedPriceList1(object);
    }

    public static Flowable<GetTradeQuotedPriceByID> getData2() {
        JSONObject object = new JSONObject();
        object.put("ID", 1000);

        return textService()
                .getTradeQuotedPriceList2(object);
    }
}