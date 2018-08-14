package com.mcy.framework.utils;

public class UploadProgress {
    long currentLength;
    long totalLength;
    boolean isOk;
    private int count;
    private int sum;

    public long getCurrentLength() {
        return currentLength;
    }

    public void setCurrentLength(long currentLength) {
        this.currentLength = currentLength;
    }

    public long getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(long totalLength) {
        this.totalLength = totalLength;
    }

    public boolean isOk() {
        return isOk;
    }

    public void setOk(boolean ok) {
        isOk = ok;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public UploadProgress(long currentLength, long totalLength, boolean isOk) {
        this.currentLength = currentLength;
        this.totalLength = totalLength;
        this.isOk = isOk;
    }

    public UploadProgress(long currentLength, long totalLength, boolean isOk, int count, int sum) {
        this.currentLength = currentLength;
        this.totalLength = totalLength;
        this.isOk = isOk;
        this.count = count;
        this.sum = sum;
    }
}
