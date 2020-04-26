package org.dreamcat.common.hc.httpclient;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.dreamcat.common.core.Wget;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Create by tuke on 2018/11/25
 */
public class HttpClientWget implements Wget<HttpUriRequest, CloseableHttpResponse> {
    private final CloseableHttpClient client;

    public HttpClientWget() {
        this(HttpClients.createDefault());
    }

    public HttpClientWget(CloseableHttpClient client) {
        this.client = client;
    }

    @Override
    public HttpUriRequest prepare(String url, String method, Map<String, String> headers) {
        return null;
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
    public void save(CloseableHttpResponse res, File file) throws IOException {
        checkStatusCode(res);
        try (FileOutputStream output = new FileOutputStream(file)) {
            HttpEntity entity = res.getEntity();
            entity.writeTo(output);
        }
    }

    @Override
    public String string(CloseableHttpResponse res) throws IOException {
        checkStatusCode(res);

        HttpEntity entity = res.getEntity();
        return EntityUtils.toString(entity, StandardCharsets.UTF_8);
    }

    @Override
    public byte[] bytes(CloseableHttpResponse res) throws IOException {
        checkStatusCode(res);

        HttpEntity entity = res.getEntity();
        return EntityUtils.toByteArray(entity);
    }

    @Override
    public InputStream inputStream(CloseableHttpResponse res) throws IOException {
        checkStatusCode(res);

        HttpEntity entity = res.getEntity();
        return entity.getContent();
    }

    @Override
    public Reader reader(CloseableHttpResponse res) throws IOException {
        checkStatusCode(res);
        HttpEntity entity = res.getEntity();
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

    private void checkStatusCode(CloseableHttpResponse res) throws IOException {
        int code = res.getStatusLine().getStatusCode();
        boolean successful = code >= 200 && code < 300;
        if (!successful) {
            throw new IOException("status code is not in [200, 300)");
        }
    }

}
