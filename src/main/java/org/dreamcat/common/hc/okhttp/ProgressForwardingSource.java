package org.dreamcat.common.hc.okhttp;

import okio.Buffer;
import okio.ForwardingSource;
import okio.Source;

import java.io.IOException;

/**
 * Create by tuke on 2018/10/16
 */
public class ProgressForwardingSource extends ForwardingSource {
    private long contentLength;
    private ProgressListener listener;
    private long byteCount;
    private boolean useByteCount = false;

    private transient long totalBytesRead = 0L;
    private transient long progressCount;

    public ProgressForwardingSource(Source source) {
        super(source);
    }

    public ProgressForwardingSource contentLength(long contentLength) {
        this.contentLength = contentLength;
        return this;
    }

    public ProgressForwardingSource listener(ProgressListener listener) {
        this.listener = listener;
        return this;
    }

    public ProgressForwardingSource useByteCount(long byteCount) {
        this.byteCount = byteCount;
        this.useByteCount = true;
        return this;
    }

    @Override
    public long read(Buffer sink, long byteCount) throws IOException {
        long bytesRead = super.read(sink, useByteCount ? this.byteCount : byteCount);
        if (bytesRead != -1) {
            totalBytesRead += bytesRead;
            progressCount++;
            listener.onProgress(totalBytesRead, contentLength, progressCount, false);
        } else {
            listener.onProgress(totalBytesRead, contentLength, progressCount, true);
        }
        return bytesRead;
    }

}
