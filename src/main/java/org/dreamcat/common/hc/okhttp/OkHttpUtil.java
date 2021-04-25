package org.dreamcat.common.hc.okhttp;

import java.io.File;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import lombok.extern.slf4j.Slf4j;
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
import okhttp3.logging.HttpLoggingInterceptor;
import org.dreamcat.common.net.SocketUtil;
import org.dreamcat.common.net.SslAlgorithm;
import org.dreamcat.common.net.SslUtil;
import org.dreamcat.common.util.ObjectUtil;

@Slf4j
public final class OkHttpUtil {

    private OkHttpUtil() {
    }

    public static final RequestBody EMPTY_BODY = newEmptyBody();

    private static final String APPLICATION_XML_UTF_8 = "application/xml; charset=UTF-8";
    private static final String APPLICATION_JSON_UTF_8 = "application/json; charset=UTF-8";
    private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    private static final String TEXT_PLAIN_UTF_8 = "text/plain; charset=UTF-8";
    private static final int TIMEOUT = 10;
    private static final String AUTHORIZATION = "Authorization";

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static RequestBody newEmptyBody() {
        return RequestBody.create(new byte[0], null);
    }

    public static RequestBody newBytesBody(byte[] data) {
        return newBytesBody(data, APPLICATION_OCTET_STREAM);
    }

    public static RequestBody newBytesBody(byte[] data, String contentType) {
        MediaType type = MediaType.parse(contentType);
        return RequestBody.create(data, type);
    }

    public static RequestBody newStringBody(String data) {
        return newStringBody(data, TEXT_PLAIN_UTF_8);
    }

    public static RequestBody newStringBody(String data, String contentType) {
        MediaType type = MediaType.parse(contentType);
        return RequestBody.create(data, type);
    }

    public static RequestBody newJSONBody(String data) {
        return newStringBody(data, APPLICATION_JSON_UTF_8);
    }

    public static RequestBody newXMLBody(String data) {
        return newStringBody(data, APPLICATION_XML_UTF_8);
    }

    public static RequestBody newFormBody(Map<String, String> map) {
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String name = entry.getKey();
            builder.add(name, map.get(name));
        }
        return builder.build();
    }

    public static RequestBody newMultipartBody(Map<String, Object> map) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String i = entry.getKey();
            Object o = map.get(i);
            if (o instanceof File) {
                File file = (File) o;
                String filename = file.getName();
                RequestBody body = RequestBody.create(file, newMediaType(filename));
                builder.addFormDataPart(i, filename, body);
            } else {
                builder.addFormDataPart(i, o.toString());
            }
        }

        // default type is multipart/mixed, so use multipart/form-data explicitly
        builder.setType(MultipartBody.FORM);
        return builder.build();
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static OkHttpClient newClient(Interceptor... interceptors) {
        return newClient(TIMEOUT, null,
                null, null, interceptors);
    }

    public static OkHttpClient newClient(Map<String, String> headers, Interceptor... interceptors) {
        return newClient(TIMEOUT, headers, null, null, interceptors);
    }

    public static OkHttpClient newClient(
            int timeout,
            Map<String, String> headers,
            String basicUsername, String basicPassword,
            Interceptor... interceptors) {
        OkHttpClient.Builder builder = newBuilder(timeout, headers, interceptors);
        if (basicUsername != null && basicPassword != null) {
            builder.authenticator((route, response) -> {
                log.info("Authenticating for response: " + response);

                if (response.request().header(AUTHORIZATION) != null) {
                    // Give up, we've already failed to authenticate.
                    return null;
                }

                String credential = Credentials.basic(basicUsername, basicPassword);
                return response.request().newBuilder()
                        .header(AUTHORIZATION, credential)
                        .build();
            });
        }

        return builder.build();
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static OkHttpClient newHttpsClient(
            String certPath, String certPassword, String keyStoreType,
            Interceptor... interceptors) throws Exception {
        return newHttpsClient(TIMEOUT, null, certPath, certPassword, keyStoreType, interceptors);
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
            String certPath, String certPassword, String keyStoreType,
            Interceptor... interceptors) throws Exception {
        OkHttpClient.Builder builder = newBuilder(timeout, headers, interceptors);

        SSLSocketFactory sslSocketFactory;
        // Create an ssl socket factory with our all-trusting manager
        X509TrustManager trustManager =  SslUtil.unsafeX509TrustManager();
        if (certPath == null || certPassword == null) {
            sslSocketFactory = SslAlgorithm.SSL.sslSocketFactoryForNoKey(
                    new TrustManager[]{ trustManager });
        } else if (keyStoreType == null) {
            sslSocketFactory = SslAlgorithm.SSL.sslSocketFactoryForBKS(certPath, certPassword);
        } else {
            sslSocketFactory = SslAlgorithm.SSL.sslSocketFactory(certPath, certPassword, keyStoreType);
        }
        builder.sslSocketFactory(sslSocketFactory, trustManager);

        return builder.build();
    }

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

    public static HttpLoggingInterceptor newHttpLoggingInterceptor() {
        return newHttpLoggingInterceptor(HttpLoggingInterceptor.Level.HEADERS, log::info);
    }

    public static HttpLoggingInterceptor newHttpLoggingInterceptor(
            HttpLoggingInterceptor.Level level) {
        return newHttpLoggingInterceptor(level, log::info);
    }

    public static HttpLoggingInterceptor newHttpLoggingInterceptor(
            HttpLoggingInterceptor.Logger logger) {
        return newHttpLoggingInterceptor(HttpLoggingInterceptor.Level.HEADERS, logger);
    }

    public static HttpLoggingInterceptor newHttpLoggingInterceptor(
            HttpLoggingInterceptor.Level level, HttpLoggingInterceptor.Logger logger) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(logger);
        interceptor.setLevel(level);
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
                if (log.isDebugEnabled()) {
                    log.debug("add cookie header {}", cookieString);
                }
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
            builder.addHeader(AUTHORIZATION, credential);
            return chain.proceed(builder.build());
        };

    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    private static OkHttpClient.Builder newBuilder(int timeout, Map<String, String> headers,
            Interceptor... interceptors) {
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
        return type;
    }
}
