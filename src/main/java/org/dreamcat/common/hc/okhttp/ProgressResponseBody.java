package org.dreamcat.common.hc.okhttp;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

/**
 * Create by tuke on 2018/10/16
 */
public class ProgressResponseBody extends ResponseBody {

    private MediaType contentType;
    private long contentLength;
    private ProgressListener listener;
    private Source source;

    private long byteCount = 8192L;

    public static ProgressResponseBody create(ResponseBody body, ProgressListener listener) {
        return new ProgressResponseBody.Builder()
                .source(body.source())
                .contentType(body.contentType())
                .contentLength(body.contentLength())
                .listener(listener)
                .build();

    }

    @Override
    public MediaType contentType() {
        return contentType;
    }

    @Override
    public long contentLength() {
        return contentLength;
    }

    @Override
    public BufferedSource source() {
        return Okio.buffer(
                new ProgressForwardingSource(source)
                        .contentLength(contentLength)
                        .listener(listener)
                        .useByteCount(byteCount));
    }

    public static class Builder {
        private ProgressResponseBody target;

        public Builder() {
            target = new ProgressResponseBody();
        }

        public Builder contentType(MediaType contentType) {
            target.contentType = contentType;
            return this;
        }

        public Builder contentLength(long contentLength) {
            target.contentLength = contentLength;
            return this;
        }

        public Builder source(Source source) {
            target.source = source;
            return this;
        }

        public Builder listener(ProgressListener listener) {
            target.listener = listener;
            return this;
        }

        public Builder byteCount(long byteCount) {
            target.byteCount = byteCount;
            return this;
        }

        public ProgressResponseBody build() {
            return target;
        }
    }
}
