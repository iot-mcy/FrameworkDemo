package com.mcy.framework.utils;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * 作者 mcy
 * 日期 2018/8/14 14:39
 */
public class ProgressRequestBody extends RequestBody {
    private final RequestBody requestBody;
    private final ProgressListener progressListener;
    private int count;
    private int sum;

    public ProgressRequestBody(RequestBody requestBody, ProgressListener progressListener) {
        this.requestBody = requestBody;
        this.progressListener = progressListener;
    }

    public ProgressRequestBody(RequestBody requestBody, ProgressListener progressListener, int count, int sum) {
        this.requestBody = requestBody;
        this.progressListener = progressListener;
        this.count = count;
        this.sum = sum;
    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        BufferedSink bufferedSink = Okio.buffer(sink(sink));
        requestBody.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            long bytesWritten = 0L;
            long contentLength = 0L;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0) {
                    contentLength = contentLength();
                }
                bytesWritten += byteCount;
                progressListener.onProgress(bytesWritten, contentLength, bytesWritten == contentLength, count, sum);
            }
        };
    }
}
