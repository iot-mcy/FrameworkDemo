package com.mcy.framework;

public class DownloadProgress {
    long currentLength = 0;
    long totalLength = 0;

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

    public DownloadProgress(long currentLength, long totalLength) {
        this.currentLength = currentLength;
        this.totalLength = totalLength;
    }
}
