package org.dreamcat.common.hc.httpclient;

import org.dreamcat.common.core.Wget;
import org.dreamcat.common.util.ThreadUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Create by tuke on 2018/11/25
 */
public class HttpClientWget implements Wget<HttpUriRequest, CloseableHttpResponse> {

    private CloseableHttpClient client;
    private ExecutorService executorService;

    public HttpClientWget() {
        this(HttpClients.createDefault());
    }

    public HttpClientWget(CloseableHttpClient client) {
        executorService = ThreadUtil.newExecutorService(getClass().getSimpleName());
        this.client = client;
    }

    @Override
    public HttpUriRequest prepare(String url, String method, Map<String, String> headers, String body) {
        return null;
    }

    @Override
    public HttpUriRequest prepare(String url, String method, Map<String, String> headers, byte[] body) {
        return null;
    }

    @Override
    public CloseableHttpResponse request(HttpUriRequest req) throws IOException {
        return client.execute(req);
    }

    @Override
    public void save(CloseableHttpResponse resp, String path) throws IOException {
        throwIfNotSuccessful(resp);

        File file = new File(path);
        try (FileOutputStream output = new FileOutputStream(file)) {
            HttpEntity entity = resp.getEntity();
            entity.writeTo(output);
        }
    }

    @Override
    public String string(CloseableHttpResponse resp) throws IOException {
        throwIfNotSuccessful(resp);

        HttpEntity entity = resp.getEntity();
        return EntityUtils.toString(entity, StandardCharsets.UTF_8);
    }

    @Override
    public byte[] bytes(CloseableHttpResponse resp) throws IOException {
        throwIfNotSuccessful(resp);

        HttpEntity entity = resp.getEntity();
        return EntityUtils.toByteArray(entity);
    }

    @Override
    public InputStream inputStream(CloseableHttpResponse resp) throws IOException {
        throwIfNotSuccessful(resp);

        HttpEntity entity = resp.getEntity();
        return entity.getContent();
    }

    @Override
    public Reader reader(CloseableHttpResponse resp) throws IOException {
        throwIfNotSuccessful(resp);
        HttpEntity entity = resp.getEntity();
        return new InputStreamReader(entity.getContent(), getCharset(entity));
    }

    private Charset getCharset(HttpEntity entity) {
        ContentType contentType = ContentType.get(entity);

        Charset charset = null;
        if (contentType != null) {
            charset = contentType.getCharset();
            if (charset == null) {
                final ContentType defaultContentType = ContentType.getByMimeType(contentType.getMimeType());
                charset = defaultContentType != null ? defaultContentType.getCharset() : null;
            }
        }
        if (charset == null) {
            charset = HTTP.DEF_CONTENT_CHARSET;
        }
        return charset;
    }

    private void throwIfNotSuccessful(CloseableHttpResponse resp) throws IOException {
        int code = resp.getStatusLine().getStatusCode();
        boolean successful = code >= 200 && code < 300;
        if (!successful) {
            throw new IOException("status code is not in [200, 300)");
        }
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public void requestAsync(HttpUriRequest req, Callback<HttpUriRequest, CloseableHttpResponse> callback) {
        executorService.submit(() -> {
            try {
                CloseableHttpResponse resp = client.execute(req);
                callback.onComplete(req, resp);
            } catch (IOException e) {
                callback.onError(req, e);
            }
        });
    }

    interface Callback<Req, Resp> {
        void onComplete(Req req, Resp resp);

        void onError(Req req, Exception e);
    }
}