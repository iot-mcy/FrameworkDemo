package com.mcy.framework.service;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by mcy on 2018/3/23.
 */

public abstract class JsonCallback<T> {
    public abstract void onFailure();

    public abstract void onResponse(T object) throws IOException;

    //获得超类的泛型参数的实际类型。
    Type getType() {
        return ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
