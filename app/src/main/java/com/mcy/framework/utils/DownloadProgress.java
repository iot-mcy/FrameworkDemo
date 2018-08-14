package com.mcy.framework.utils;

public class DownloadProgress {
    long currentLength = 0;
    long totalLength = 0;
    boolean isOk;

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

    public DownloadProgress(long currentLength, long totalLength, boolean isOk) {
        this.currentLength = currentLength;
        this.totalLength = totalLength;
        this.isOk = isOk;
    }
}
