package org.dreamcat.common.hc.httpclient;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.dreamcat.common.util.ObjectUtil;

/**
 * Create by tuke on 2018/11/25
 */
public final class HttpClientUtil {

    private HttpClientUtil() {
    }

    private static final String APPLICATION_XML_UTF_8 = "application/xml; charset=UTF-8";
    private static final String APPLICATION_JSON_UTF_8 = "application/json; charset=UTF-8";
    private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    private static final String TEXT_PLAIN_UTF_8 = "text/plain; charset=UTF-8";
    private static final HttpLoggingInterceptor HTTP_LOGGING_INTERCEPTOR = new HttpLoggingInterceptor();

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static HttpEntity newBytesBody(byte[] data) {
        return newBytesBody(data, APPLICATION_OCTET_STREAM);
    }

    public static HttpEntity newBytesBody(byte[] data, String contentType) {
        ByteArrayEntity entity = new ByteArrayEntity(data);
        entity.setContentType(contentType);
        return entity;
    }

    public static HttpEntity newStringBody(String data) {
        return newStringBody(data, TEXT_PLAIN_UTF_8);
    }

    public static HttpEntity newStringBody(String data, String contentType) {
        StringEntity entity = new StringEntity(data, StandardCharsets.UTF_8);
        entity.setContentType(contentType);
        return entity;
    }

    public static HttpEntity newJSONBody(String data) {
        return newStringBody(data, APPLICATION_JSON_UTF_8);
    }

    public static HttpEntity newXMLBody(String data) {
        return newStringBody(data, APPLICATION_XML_UTF_8);
    }

    public static HttpEntity newFormBody(Map<String, String> form)
            throws UnsupportedEncodingException {
        List<NameValuePair> pairList = form.entrySet().stream()
                .map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return new UrlEncodedFormEntity(pairList);
    }

    public static HttpEntity newMultipartBody(Map<String, Object> multipart) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        for (Map.Entry<String, Object> entry : multipart.entrySet()) {
            String i = entry.getKey();
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
        return newClient(false);
    }

    public static CloseableHttpClient newClient(boolean logging) {
        if (!logging) {
            return newClient(null, null);
        }

        return newClient(
                null,
                Collections.singletonList(HTTP_LOGGING_INTERCEPTOR),
                Collections.singletonList(HTTP_LOGGING_INTERCEPTOR));
    }

    public static CloseableHttpClient newClient(
            String basicUsername, String basicPassword) {
        return newClient(null, null, null, basicUsername, basicPassword,
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

        // BASIC AUTH
        if (basicUsername != null && basicPassword != null) {
            UsernamePasswordCredentials credentials =
                    new UsernamePasswordCredentials(basicUsername, basicPassword);
            CredentialsProvider provider = new BasicCredentialsProvider();
            AuthScope scope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT,
                    AuthScope.ANY_REALM);
            provider.setCredentials(scope, credentials);
            builder.setDefaultCredentialsProvider(provider);
        }

        if (connectionConfig != null) builder.setDefaultConnectionConfig(connectionConfig);
        if (socketConfig != null) builder.setDefaultSocketConfig(socketConfig);
        if (userAgent != null) builder.setUserAgent(userAgent);

        return builder.build();
    }

}
