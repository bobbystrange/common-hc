package org.dreamcat.common.hc.httpclient;

import org.dreamcat.common.annotation.NotNull;
import org.dreamcat.common.annotation.Nullable;
import org.dreamcat.common.util.ObjectUtil;
import org.dreamcat.common.util.URLUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Create by tuke on 2018/11/25
 */
public class HttpClientUtil {

    private static final String APPLICATION_XML_UTF_8 = "application/xml; charset=UTF-8";
    private static final String APPLICATION_JSON_UTF_8 = "application/json; charset=UTF-8";
    private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    private static final String TEXT_PLAIN_UTF_8 = "text/plain; charset=UTF-8";
    private static final HttpLoggingInterceptor HTTP_LOGGING_INTERCEPTOR = new HttpLoggingInterceptor();
    private static final CloseableHttpClient client = newClient();

    public static CloseableHttpResponse get(
            String url,
            @Nullable Map<String, String> headers,
            @Nullable Map<String, String> queryMap) throws IOException {
        if (ObjectUtil.isNotEmpty(queryMap)) {
            url = URLUtil.concatUrl(url, queryMap);
        }
        return request(url, null, headers, queryMap, HttpGet::new);
    }

    public static CloseableHttpResponse post(
            String url,
            HttpEntity entity,
            @Nullable Map<String, String> headers,
            @Nullable Map<String, String> queryMap) throws IOException {
        return request(url, entity, headers, queryMap, HttpPost::new);
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static CloseableHttpResponse put(
            String url,
            HttpEntity entity,
            @Nullable Map<String, String> headers,
            @Nullable Map<String, String> queryMap) throws IOException {
        return request(url, entity, headers, queryMap, HttpPut::new);
    }

    public static CloseableHttpResponse delete(
            String url,
            @Nullable Map<String, String> headers,
            @Nullable Map<String, String> queryMap) throws IOException {
        if (ObjectUtil.isNotEmpty(queryMap)) {
            url = URLUtil.concatUrl(url, queryMap);
        }
        return request(url, null, headers, queryMap, HttpDelete::new);
    }

    public static CloseableHttpResponse head(
            String url,
            @Nullable Map<String, String> headers,
            @Nullable Map<String, String> queryMap) throws IOException {
        if (ObjectUtil.isNotEmpty(queryMap)) {
            url = URLUtil.concatUrl(url, queryMap);
        }
        return request(url, null, headers, queryMap, HttpHead::new);
    }

    public static CloseableHttpResponse request(
            String url,
            String method,
            @Nullable HttpEntity entity,
            @Nullable Map<String, String> headers,
            @Nullable Map<String, String> queryMap) throws IOException {
        Function<String, HttpUriRequest> constructor;
        method = method.toUpperCase();
        switch (method) {
            default:
                throw new IllegalArgumentException("no supported http method " + method);
            case "GET":
                constructor = HttpGet::new;
                break;
            case "POST":
                constructor = HttpPost::new;
                break;
            case "PUT":
                constructor = HttpPut::new;
                break;
            case "DELETE":
                constructor = HttpDelete::new;
                break;
        }
        return request(url, entity, headers, queryMap, constructor);
    }

    public static CloseableHttpResponse request(
            String url,
            @Nullable HttpEntity entity,
            @Nullable Map<String, String> headers,
            @Nullable Map<String, String> queryMap,
            Function<String, HttpUriRequest> constructor) throws IOException {
        if (ObjectUtil.isNotEmpty(queryMap)) {
            url = URLUtil.concatUrl(url, queryMap);
        }

        HttpUriRequest req = constructor.apply(url);
        if (ObjectUtil.isNotEmpty(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                req.addHeader(entry.getKey(), entry.getValue());
            }
        }

        if (entity != null && req instanceof HttpEntityEnclosingRequest) {
            ((HttpEntityEnclosingRequest) req).setEntity(entity);
        }
        return request(req);
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static CloseableHttpResponse request(HttpUriRequest req) throws IOException {
        return client.execute(req);
    }

    public static HttpEntity newJSONEntity(String data) {
        StringEntity entity = new StringEntity(data, StandardCharsets.UTF_8);
        entity.setContentType(APPLICATION_JSON_UTF_8);
        return entity;
    }

    public static HttpEntity newXMLEntity(String data) {
        StringEntity entity = new StringEntity(data, StandardCharsets.UTF_8);
        entity.setContentType(APPLICATION_XML_UTF_8);
        return entity;
    }

    public static HttpEntity newStringEntity(String data, String contentType) {
        StringEntity entity = new StringEntity(data, StandardCharsets.UTF_8);
        entity.setContentType(contentType);
        return entity;
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static HttpEntity newFormEntity(Map<String, String> form) throws UnsupportedEncodingException {
        List<NameValuePair> pairList = form.entrySet().stream()
                .map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return new UrlEncodedFormEntity(pairList);
    }
    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static HttpEntity newMultipartEntity(Map<String, Object> multipart) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        for (String i : multipart.keySet()) {
            Object o = multipart.get(i);
            if (o instanceof InputStream) {
                InputStream input = (InputStream) o;
                builder.addBinaryBody(i, input);
            } else if (o instanceof File) {
                File file = (File) o;
                builder.addBinaryBody(i, file);
            } else if (o instanceof byte[]) {
                byte[] bytes = (byte[]) o;
                builder.addBinaryBody(i, bytes);
            } else {
                String text = o.toString();
                builder.addTextBody(i, text);
            }
        }
        return builder.build();
    }

    public static CloseableHttpClient newClient() {
        return newClient(null, (String) null, (String) null);
    }

    public static CloseableHttpClient newClient(
            @Nullable String userAgent,
            @Nullable String basicUsername, @Nullable String basicPassword) {
        return newClient(null, null, userAgent, basicUsername, basicPassword,
                null, null);
    }

    public static CloseableHttpClient newClient(
            @Nullable String userAgent,
            @Nullable List<HttpRequestInterceptor> requestInterceptors,
            @Nullable List<HttpResponseInterceptor> responseInterceptors) {
        return newClient(null, null, userAgent, null, null,
                requestInterceptors, responseInterceptors);
    }

    public static CloseableHttpClient newClient(
            @Nullable ConnectionConfig connectionConfig,
            @Nullable SocketConfig socketConfig,
            @Nullable String userAgent,
            @Nullable String basicUsername, @Nullable String basicPassword,
            @Nullable List<HttpRequestInterceptor> requestInterceptors,
            @Nullable List<HttpResponseInterceptor> responseInterceptors) {
        HttpClientBuilder builder = HttpClientBuilder.create();

        if (ObjectUtil.isNotEmpty(requestInterceptors)) {
            for (HttpRequestInterceptor i : requestInterceptors) {
                builder.addInterceptorLast(i);
            }
        }

        if (ObjectUtil.isNotEmpty(responseInterceptors)) {
            for (HttpResponseInterceptor i : responseInterceptors) {
                builder.addInterceptorLast(i);
            }
        }
        // LOG
        builder.addInterceptorLast(httpRequestInterceptor());
        builder.addInterceptorLast(httpResponseInterceptor());

        // BASIC AUTH
        if (basicUsername != null & basicPassword != null) {
            UsernamePasswordCredentials credentials =
                    new UsernamePasswordCredentials(basicUsername, basicPassword);
            CredentialsProvider provider = new BasicCredentialsProvider();
            AuthScope scope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM);
            provider.setCredentials(scope, credentials);
            builder.setDefaultCredentialsProvider(provider);
        }

        if (connectionConfig != null) builder.setDefaultConnectionConfig(connectionConfig);
        if (socketConfig != null) builder.setDefaultSocketConfig(socketConfig);
        if (userAgent != null) builder.setUserAgent(userAgent);

        return builder.build();
    }

    public static String toString(@NotNull CloseableHttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        if (entity == null) return null;
        //         ContentType contentType = ContentType.getOrDefault(entity);
        return EntityUtils.toString(entity);
    }

    private static HttpRequestInterceptor httpRequestInterceptor() {
        return HTTP_LOGGING_INTERCEPTOR;
    }

    private static HttpResponseInterceptor httpResponseInterceptor() {
        return HTTP_LOGGING_INTERCEPTOR;
    }

}
