package org.dreamcat.common.hc.httpclient;

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
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.dreamcat.common.util.ObjectUtil;
import org.dreamcat.common.util.UrlUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
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

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static CloseableHttpResponse get(String url) throws IOException {
        return get(url, null);
    }

    public static CloseableHttpResponse get(
            String url,
            Map<String, String> headers) throws IOException {
        return get(url, headers, null);
    }

    public static CloseableHttpResponse get(
            String url,
            Map<String, String> headers,
            Map<String, String> queryMap) throws IOException {
        if (ObjectUtil.isNotEmpty(queryMap)) {
            url = UrlUtil.concatUrl(url, queryMap);
        }
        return request(url, "GET", null, headers, queryMap);
    }

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

    public static CloseableHttpResponse postJSON(
            String url,
            String jsonData) throws IOException {
        return postJSON(url, jsonData, null);
    }

    public static CloseableHttpResponse postJSON(
            String url,
            String jsonData,
            Map<String, String> headers) throws IOException {
        return postJSON(url, jsonData, headers, null);
    }

    public static CloseableHttpResponse postJSON(
            String url,
            String jsonData,
            Map<String, String> headers,
            Map<String, String> queryMap) throws IOException {
        return post(url, newJSONBody(jsonData), headers, queryMap);
    }

    public static CloseableHttpResponse postXML(
            String url,
            String xmlData) throws IOException {
        return postXML(url, xmlData, null);
    }

    public static CloseableHttpResponse postXML(
            String url,
            String xmlData,
            Map<String, String> headers) throws IOException {
        return postXML(url, xmlData, headers, null);
    }

    public static CloseableHttpResponse postXML(
            String url,
            String xmlData,
            Map<String, String> headers,
            Map<String, String> queryMap) throws IOException {
        return post(url, newXMLBody(xmlData), headers, queryMap);
    }

    public static CloseableHttpResponse postForm(
            String url,
            Map<String, String> formData) throws IOException {
        return postForm(url, formData, null);
    }

    public static CloseableHttpResponse postForm(
            String url,
            Map<String, String> formData,
            Map<String, String> headers) throws IOException {
        return postForm(url, formData, headers, null);
    }

    public static CloseableHttpResponse postForm(
            String url,
            Map<String, String> formData,
            Map<String, String> headers,
            Map<String, String> queryMap) throws IOException {
        return post(url, newFormBody(formData), headers, queryMap);
    }

    public static CloseableHttpResponse postMultipartForm(
            String url,
            Map<String, Object> formData) throws IOException {
        return postMultipartForm(url, formData, null);
    }

    public static CloseableHttpResponse postMultipartForm(
            String url,
            Map<String, Object> formData,
            Map<String, String> headers) throws IOException {
        return postMultipartForm(url, formData, headers, null);
    }

    public static CloseableHttpResponse postMultipartForm(
            String url,
            Map<String, Object> formData,
            Map<String, String> headers,
            Map<String, String> queryMap) throws IOException {
        return post(url, newMultipartBody(formData), headers, queryMap);
    }

    public static CloseableHttpResponse post(
            String url,
            HttpEntity entity) throws IOException {
        return post(url, entity, null);
    }

    public static CloseableHttpResponse post(
            String url,
            HttpEntity entity,
            Map<String, String> headers) throws IOException {
        return post(url, entity, headers, null);
    }

    public static CloseableHttpResponse post(
            String url,
            HttpEntity entity,
            Map<String, String> headers,
            Map<String, String> queryMap) throws IOException {
        return request(url, "POST", entity, headers, queryMap);
    }


    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

    /**
     * @param url     url
     * @param method  http method
     * @param entity  only works in Post/Put/Patch
     * @param headers extra headers
     * @return response
     * @throws IOException http I/O error
     */
    public static CloseableHttpResponse request(
            String url,
            String method,
            HttpEntity entity,
            Map<String, String> headers) throws IOException {
        return request(url, method, entity, headers, null);
    }

    public static CloseableHttpResponse request(
            String url,
            String method,
            HttpEntity entity,
            Map<String, String> headers,
            Map<String, String> queryMap) throws IOException {
        if (ObjectUtil.isNotEmpty(queryMap)) {
            url = UrlUtil.concatUrl(url, queryMap);
        }

        method = method.toUpperCase();
        HttpUriRequest req;
        switch (method) {
            default:
                throw new IllegalArgumentException("no supported http method " + method);
            case "GET":
                req = new HttpGet(url);
                break;
            case "POST":
                req = new HttpPost(url);
                break;
            case "PUT":
                req = new HttpPut(url);
                break;
            case "DELETE":
                req = new HttpDelete(url);
                break;
            case "HEAD":
                req = new HttpHead(url);
                break;
            case "OPTIONS":
                req = new HttpOptions(url);
                break;
            case "PATCH":
                req = new HttpPatch(url);
                break;
            case "TRACE":
                req = new HttpTrace(url);
                break;
        }

        if (ObjectUtil.isNotEmpty(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                req.addHeader(entry.getKey(), entry.getValue());
            }
        }

        if (entity != null && req instanceof HttpEntityEnclosingRequest) {
            ((HttpEntityEnclosingRequest) req).setEntity(entity);
        }
        return client.execute(req);

    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static HttpEntity newJSONBody(String data) {
        StringEntity entity = new StringEntity(data, StandardCharsets.UTF_8);
        entity.setContentType(APPLICATION_JSON_UTF_8);
        return entity;
    }

    public static HttpEntity newXMLBody(String data) {
        StringEntity entity = new StringEntity(data, StandardCharsets.UTF_8);
        entity.setContentType(APPLICATION_XML_UTF_8);
        return entity;
    }

    public static HttpEntity newStringBody(String data, String contentType) {
        StringEntity entity = new StringEntity(data, StandardCharsets.UTF_8);
        entity.setContentType(contentType);
        return entity;
    }

    public static HttpEntity newFormBody(Map<String, String> form) throws UnsupportedEncodingException {
        List<NameValuePair> pairList = form.entrySet().stream()
                .map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return new UrlEncodedFormEntity(pairList);
    }

    public static HttpEntity newMultipartBody(Map<String, Object> multipart) {
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

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static CloseableHttpClient newClient() {
        return newClient(null, (String) null, (String) null);
    }

    public static CloseableHttpClient newClient(
            String userAgent,
            String basicUsername, String basicPassword) {
        return newClient(null, null, userAgent, basicUsername, basicPassword,
                null, null);
    }

    public static CloseableHttpClient newClient(
            String userAgent,
            List<HttpRequestInterceptor> requestInterceptors,
            List<HttpResponseInterceptor> responseInterceptors) {
        return newClient(null, null, userAgent, null, null,
                requestInterceptors, responseInterceptors);
    }

    public static CloseableHttpClient newClient(
            ConnectionConfig connectionConfig,
            SocketConfig socketConfig,
            String userAgent,
            String basicUsername, String basicPassword,
            List<HttpRequestInterceptor> requestInterceptors,
            List<HttpResponseInterceptor> responseInterceptors) {
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

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    private static HttpRequestInterceptor httpRequestInterceptor() {
        return HTTP_LOGGING_INTERCEPTOR;
    }

    private static HttpResponseInterceptor httpResponseInterceptor() {
        return HTTP_LOGGING_INTERCEPTOR;
    }

}
