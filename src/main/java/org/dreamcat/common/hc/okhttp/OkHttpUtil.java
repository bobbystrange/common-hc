package org.dreamcat.common.hc.okhttp;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import org.dreamcat.common.net.SocketUtil;
import org.dreamcat.common.util.ObjectUtil;
import org.dreamcat.common.util.UrlUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class OkHttpUtil {

    private static final String APPLICATION_XML_UTF_8 = "application/xml; charset=UTF-8";
    private static final String APPLICATION_JSON_UTF_8 = "application/json; charset=UTF-8";
    private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    private static final String TEXT_PLAIN_UTF_8 = "text/plain; charset=UTF-8";

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----
    // TimeUnit.SECONDS
    private static final int TIMEOUT = 10;
    private static final HttpLoggingInterceptor httpLoggingInterceptor
            = newHttpLoggingInterceptor();
    private static final OkHttpClient client = newClient();

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

    public static Response get(String url) throws IOException {
        return get(url, null, null);
    }

    public static Response get(
            String url,
            Map<String, String> headers,
            Map<String, String> queryMap) throws IOException {
        if (queryMap != null && !queryMap.isEmpty()) {
            url = UrlUtil.concatUrl(url, queryMap);
        }
        Request.Builder builder = new Request.Builder()
                .get()
                .url(url);
        if (headers != null && !headers.isEmpty()) {
            for (String name : headers.keySet()) {
                builder.addHeader(name, headers.get(name));
            }
        }
        return request(builder.build());
    }

    public static void getAsync(String url, Callback callback) {
        getAsync(url, null, null, callback);
    }

    public static void getAsync(
            String url,
            Map<String, String> headers,
            Map<String, String> queryMap,
            Callback callback) {
        if (queryMap != null && !queryMap.isEmpty()) {
            url = UrlUtil.concatUrl(url, queryMap);
        }
        Request.Builder builder = new Request.Builder()
                .url(url);
        if (headers != null && !headers.isEmpty()) {
            for (String name : headers.keySet()) {
                builder.addHeader(name, headers.get(name));
            }
        }
        requestAsync(builder.build(), callback);
    }

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

    public static Response postJSON(
            String url,
            String jsonData,
            Map<String, String> headers,
            Map<String, String> queryMap) throws IOException {
        return post(url, newStringBody(jsonData, APPLICATION_JSON_UTF_8), headers, queryMap);
    }

    public static Response postXML(
            String url,
            String xmlData,
            Map<String, String> headers,
            Map<String, String> queryMap) throws IOException {
        return post(url, newStringBody(xmlData, APPLICATION_XML_UTF_8), headers, queryMap);
    }

    public static Response postForm(
            String url,
            Map<String, String> form,
            Map<String, String> headers,
            Map<String, String> queryMap) throws IOException {
        return post(url, newFormBody(form), headers, queryMap);
    }

    public static Response post(
            String url,
            RequestBody body) throws IOException {
        return post(url, body, null, null);
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static Response post(
            String url,
            RequestBody body,
            Map<String, String> headers,
            Map<String, String> queryMap) throws IOException {
        if (queryMap != null && !queryMap.isEmpty()) {
            url = UrlUtil.concatUrl(url, queryMap);
        }
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(body);
        if (headers != null && !headers.isEmpty()) {
            for (String name : headers.keySet()) {
                builder.addHeader(name, headers.get(name));
            }
        }
        return request(builder.build());
    }

    public static void postAsync(String url, RequestBody body, Callback callback) {
        postAsync(url, body, null, null, callback);
    }

    public static void postAsync(
            String url,
            RequestBody body,
            Map<String, String> headers,
            Map<String, String> queryMap,
            Callback callback) {
        if (queryMap != null && !queryMap.isEmpty()) {
            url = UrlUtil.concatUrl(url, queryMap);
        }
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(body);
        if (headers != null && !headers.isEmpty()) {
            for (String name : headers.keySet()) {
                builder.addHeader(name, headers.get(name));
            }
        }
        requestAsync(builder.build(), callback);
    }

    public static Response request(
            String url,
            String method,
            RequestBody body,
            Map<String, String> headers,
            Map<String, String> queryMap) throws IOException {
        if (queryMap != null && !queryMap.isEmpty()) {
            url = UrlUtil.concatUrl(url, queryMap);
        }

        if ("GET".equalsIgnoreCase(method)) {
            return get(url, headers, queryMap);
        }
        Request.Builder builder = new Request.Builder()
                .url(url)
                .method(method.toUpperCase(), body);
        if (headers != null && !headers.isEmpty()) {
            for (String name : headers.keySet()) {
                builder.addHeader(name, headers.get(name));
            }
        }
        return request(builder.build());
    }

    public static void requestAsync(
            String url,
            String method,
            RequestBody body,
            Map<String, String> headers,
            Map<String, String> queryMap,
            Callback callback) {
        if (queryMap != null && !queryMap.isEmpty()) {
            url = UrlUtil.concatUrl(url, queryMap);
        }
        Request.Builder builder = new Request.Builder()
                .url(url)
                .method(method.toUpperCase(), body);
        if (headers != null && !headers.isEmpty()) {
            for (String name : headers.keySet()) {
                builder.addHeader(name, headers.get(name));
            }
        }
        requestAsync(builder.build(), callback);
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static Response request(Request req) throws IOException {
        return client.newCall(req).execute();
    }

    public static void requestAsync(Request req, Callback callback) {
        client.newCall(req).enqueue(callback);
    }

    public static RequestBody newBytesBody(String data) {
        MediaType type = MediaType.parse(APPLICATION_OCTET_STREAM);
        return RequestBody.create(type, data);
    }

    public static RequestBody newBytesBody(byte[] data) {
        MediaType type = MediaType.parse(APPLICATION_OCTET_STREAM);
        return RequestBody.create(type, data);
    }

    public static RequestBody newStringBody(String data, String mediaType) {
        if (mediaType == null) mediaType = TEXT_PLAIN_UTF_8;
        MediaType type = MediaType.parse(mediaType);
        return RequestBody.create(type, data);
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static RequestBody newFormBody(Map<String, String> map) {
        FormBody.Builder builder = new FormBody.Builder();
        for (String name : map.keySet()) {
            builder.add(name, map.get(name));
        }
        return builder.build();
    }

    public static RequestBody newMultipartBody(Map<String, Object> map) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        for (String i : map.keySet()) {
            Object o = map.get(i);
            if (o instanceof File) {
                File file = (File) o;
                String filename = file.getName();
                RequestBody body = RequestBody.create(newMediaType(filename), file);
                builder.addFormDataPart(i, filename, body);
            } else {
                builder.addFormDataPart(i, o.toString());
            }
        }
        return builder.build();
    }

    public static OkHttpClient newClient() {
        return newClient(null, null, null);
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static OkHttpClient newClient(
            Map<String, String> headers,
            String basicUsername,
            String basicPassword) {
        return newClient(TIMEOUT, headers, basicUsername, basicPassword, httpLoggingInterceptor);
    }

    public static OkHttpClient newClient(
            int timeout,
            Map<String, String> headers,
            String basicUsername,
            String basicPassword,
            Interceptor... interceptors) {
        return newBuilder(timeout, headers, interceptors)
                .authenticator((route, response) -> {
                    log.info("Authenticating for response: " + response);

                    if (response.request().header("Authorization") != null) {
                        // Give up, we've already failed to authenticate.
                        return null;
                    }

                    String credential = Credentials.basic(basicUsername, basicPassword);
                    return response.request().newBuilder()
                            .header("Authorization", credential)
                            .build();
                })
                .build();
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static OkHttpClient newHttpsClient(
            String certPath,
            String certPassword,
            String keyStoreType) throws Exception {
        return newHttpsClient(TIMEOUT, null, certPath, certPassword, keyStoreType, httpLoggingInterceptor);
    }

    /**
     * use the cert
     *
     * @param timeout      second unit
     * @param headers      headers
     * @param interceptors extra interceptors
     * @param certPath     cert file path
     * @param certPassword cert file password
     * @param keyStoreType default value is <strong>BKS</strong>
     * @return client
     * @throws Exception any cert error
     */
    public static OkHttpClient newHttpsClient(
            int timeout,
            Map<String, String> headers,
            String certPath,
            String certPassword,
            String keyStoreType,
            Interceptor... interceptors) throws Exception {
        OkHttpClient.Builder builder = newBuilder(timeout, headers, interceptors);
        if (keyStoreType == null)
            builder.sslSocketFactory(
                    SocketUtil.sslSocketFactoryForBKS(certPath, certPassword),
                    SocketUtil.x509TrustManager());
        else builder.sslSocketFactory(
                SocketUtil.sslSocketFactory(certPath, certPassword, keyStoreType),
                SocketUtil.x509TrustManager());
        return builder.build();
    }

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

    public static HttpLoggingInterceptor newHttpLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(
                message -> {
                    log.info("**** **** ****:\t{}", message);
                });
        interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        return interceptor;
    }

    public static Interceptor newCookieInterceptor(final CookieJar cookieJar) {
        return chain -> {
            Request request = chain.request();
            Request.Builder builder = request.newBuilder();

            List<Cookie> cookies = cookieJar.loadForRequest(request.url());
            if (!cookies.isEmpty()) {
                Cookie cookie = cookies.get(0);
                String cookieString = String.format("%s=%s", cookie.name(), cookie.value());
                log.info("add cookie header {}", cookieString);
                builder.addHeader("Cookie", cookieString);
            }
            return chain.proceed(builder.build());
        };
    }

    public static Interceptor newBasicInterceptor(String basicUsername, String basicPassword) {
        return chain -> {
            Request request = chain.request();
            Request.Builder builder = request.newBuilder();

            String credential = Credentials.basic(basicUsername, basicPassword);
            builder.addHeader("Authorization", credential);
            return chain.proceed(builder.build());
        };

    }

    public static void save(ResponseBody body, String path) throws IOException {
        File file = new File(path);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] buf = body.bytes();
            fos.write(buf);
        }
    }

    public static String getResponseString(Response response) throws IOException {
        ResponseBody responseBody = response.body();
        String result = null;
        if (responseBody != null) {
            result = responseBody.string();
            responseBody.close();
        }
        return result;
    }

    private static OkHttpClient.Builder newBuilder(int timeout, Map<String, String> headers, Interceptor... interceptors) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .cookieJar(new CookieJarImpl());

        if (ObjectUtil.isNotEmpty(interceptors)) {
            for (Interceptor i : interceptors) {
                if (i == null) continue;
                builder.addInterceptor(i);
            }
        }

        if (headers != null && headers.size() > 0)
            builder.addInterceptor(new GlobalRequestInterceptor(headers));
        return builder;
    }

    private static MediaType newMediaType(String filename) {
        String defaultContentType = APPLICATION_OCTET_STREAM;
        String contentType = URLConnection.getFileNameMap().getContentTypeFor(filename);
        if (contentType == null) return MediaType.parse(defaultContentType);
        MediaType type = MediaType.parse(contentType);
        if (type == null) return MediaType.parse(defaultContentType);
        ;
        return type;
    }
}
