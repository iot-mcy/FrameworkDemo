package com.mcy.framework;

public abstract class HttpCallBack {
    abstract void onLoading(long currentLength, long totalLength);
    abstract void onError(String s);
}
