package org.dreamcat.common.hc.httpclient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.dreamcat.common.core.Wget;
import org.dreamcat.common.util.ObjectUtil;

/**
 * Create by tuke on 2018/11/25
 */
@Slf4j
public class HttpClientWget implements Wget<HttpUriRequest, CloseableHttpResponse> {

    private final CloseableHttpClient client;

    public HttpClientWget() {
        this(false);
    }

    public HttpClientWget(boolean logging) {
        this(HttpClientUtil.newClient(logging));
    }

    public HttpClientWget(CloseableHttpClient client) {
        this.client = client;
    }

    public HttpUriRequest prepare(String url, String method, Map<String, String> headers,
            HttpEntity entity) {
        method = method.toUpperCase();
        HttpUriRequest request;
        switch (method) {
            case "GET":
                request = new HttpGet(url);
                break;
            case "POST":
                request = new HttpPost(url);
                break;
            case "PUT":
                request = new HttpPut(url);
                break;
            case "DELETE":
                request = new HttpDelete(url);
                break;
            case "HEAD":
                request = new HttpHead(url);
                break;
            case "OPTIONS":
                request = new HttpOptions(url);
                break;
            case "PATCH":
                request = new HttpPatch(url);
                break;
            case "TRACE":
                request = new HttpTrace(url);
                break;
            default:
                throw new IllegalArgumentException("No such HTTP method: " + method);
        }

        if (ObjectUtil.isNotEmpty(headers)) {
            headers.forEach(request::addHeader);
        }

        if (entity != null && request instanceof HttpEntityEnclosingRequest) {
            ((HttpEntityEnclosingRequest) request).setEntity(entity);
        }
        return request;
    }

    @Override
    public HttpUriRequest prepare(String url, String method, Map<String, String> headers) {
        return prepare(url, method, headers, (HttpEntity) null);
    }

    @Override
    public HttpUriRequest prepare(String url, String method, Map<String, String> headers,
            String body, String contentType) {
        return prepare(url, method, headers, HttpClientUtil.newStringBody(body, contentType));
    }

    @Override
    public HttpUriRequest prepare(String url, String method, Map<String, String> headers,
            byte[] body, String contentType) {
        return prepare(url, method, headers, HttpClientUtil.newBytesBody(body, contentType));
    }

    @Override
    public CloseableHttpResponse request(HttpUriRequest request) throws IOException {
        return client.execute(request);
    }

    @Override
    public String string(CloseableHttpResponse response) throws IOException {
        checkStatusCode(response);

        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity, StandardCharsets.UTF_8);
    }

    @Override
    public byte[] bytes(CloseableHttpResponse response) throws IOException {
        checkStatusCode(response);

        HttpEntity entity = response.getEntity();
        return EntityUtils.toByteArray(entity);
    }

    /**
     * use 4096 buffer size
     *
     * @see BasicHttpEntity#writeTo(OutputStream)
     */
    @Override
    public boolean save(CloseableHttpResponse response, File file) throws IOException {
        checkStatusCode(response);
        try (FileOutputStream output = new FileOutputStream(file)) {
            HttpEntity entity = response.getEntity();
            if (entity == null) return false;

            entity.writeTo(output);
            return true;
        }
    }

    @Override
    public boolean saveTo(CloseableHttpResponse response, File dir) throws IOException {
        Header[] headers = response.getHeaders("Content-Disposition");
        if (ObjectUtil.isEmpty(headers)) {
            log.warn("HTTP Header Content-Disposition is not found");
            return false;
        }

        HeaderElement[] headerElements = headers[0].getElements();
        if (ObjectUtil.isEmpty(headerElements)) {
            log.warn("HTTP Header Content-Disposition has no elements");
            return false;
        }

        NameValuePair disposition = headerElements[0].getParameterByName("filename");
        String filename;
        if (disposition == null || (filename = disposition.getValue()) == null) {
            log.warn("HTTP Header Content-Disposition has no field called filename");
            return false;
        }
        return save(response, new File(dir, filename));
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    @Override
    public void requestAsync(HttpUriRequest request,
            Callback<HttpUriRequest, CloseableHttpResponse> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream inputStream(CloseableHttpResponse response) throws IOException {
        checkStatusCode(response);

        HttpEntity entity = response.getEntity();
        return entity.getContent();
    }

    @Override
    public Reader reader(CloseableHttpResponse response) throws IOException {
        checkStatusCode(response);
        HttpEntity entity = response.getEntity();
        return new InputStreamReader(entity.getContent(), getCharset(entity));
    }

    @Override
    public CloseableHttpResponse postForm(String url, Map<String, String> headers,
            Map<String, String> form) throws IOException {
        return request(prepare(url, "POST", headers, HttpClientUtil.newFormBody(form)));
    }

    @Override
    public CloseableHttpResponse postFormData(String url, Map<String, String> headers,
            Map<String, Object> formData) throws IOException {
        return null;
    }

    private Charset getCharset(HttpEntity entity) {
        ContentType contentType = ContentType.get(entity);

        Charset charset = null;
        if (contentType != null) {
            charset = contentType.getCharset();
            if (charset == null) {
                final ContentType defaultContentType = ContentType
                        .getByMimeType(contentType.getMimeType());
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
