package org.dreamcat.common.hc.okhttp;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Create by tuke on 2018/10/16
 */
@AllArgsConstructor
@NoArgsConstructor
public class ProgressRequestBody extends RequestBody {

    private MediaType contentType;
    private long contentLength;
    private ProgressListener listener;
    private InputStream istream;

    private long byteCount = 4096;

    @Override
    public MediaType contentType() {
        return contentType;
    }

    @Override
    public long contentLength() {
        return contentLength;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        Source source = Okio.source(istream);
        Buffer buf = new Buffer();
        long total = contentLength();
        long current = 0L;

        long readCount;
        long progressCount = 0L;
        while ((readCount = source.read(buf, byteCount)) != -1) {
            sink.write(buf, readCount);
            current += readCount;
            progressCount++;
            listener.onProgress(total, current, progressCount, false);
        }
        listener.onProgress(total, current, progressCount, true);
    }

    public static class Builder {
        private ProgressRequestBody target;

        public Builder() {
            target = new ProgressRequestBody();
        }

        public Builder byteCount(long byteCount) {
            target.byteCount = byteCount;
            return this;
        }


        public Builder contentType(MediaType contentType) {
            target.contentType = contentType;
            return this;
        }

        public Builder listener(ProgressListener listener) {
            target.listener = listener;
            return this;
        }

        public Builder istream(byte[] data) {
            target.istream = new ByteArrayInputStream(data);
            target.contentLength = data.length;
            return this;
        }

        public Builder istream(File file) throws FileNotFoundException {
            target.istream = new FileInputStream(file);
            target.contentLength = file.length();
            return this;
        }

        public Builder istream(InputStream istream, long contentLength) {
            target.istream = istream;
            target.contentLength = contentLength;
            return this;
        }

        public ProgressRequestBody build() {
            return target;
        }
    }

}







