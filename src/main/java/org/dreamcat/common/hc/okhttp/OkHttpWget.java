package org.dreamcat.common.hc.okhttp;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.dreamcat.common.core.Wget;
import org.dreamcat.common.util.ObjectUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class OkHttpWget implements Wget<Request, Response> {

    private static final String TEXT_PLAIN_UTF_8 = "text/plain; charset=UTF-8";

    private final OkHttpClient client;

    public OkHttpWget() {
        this(OkHttpUtil.newClient());
    }

    public OkHttpWget(OkHttpClient client) {
        this.client = client;
    }

    public Response requestUrl(String url) throws IOException {
        Request req = new Request.Builder()
                .url(url)
                .build();
        return request(req);
    }

    @Override
    public Request prepare(String url, String method, Map<String, String> headers, String body) {
        String mediaType = null;
        if (ObjectUtil.isNotEmpty(headers)) {
            mediaType = headers.entrySet().stream()
                    .collect(Collectors.toMap(
                            entry -> entry.getKey().toLowerCase(),
                            Map.Entry::getValue))
                    .get("content-type");
        }
        RequestBody requestBody = OkHttpUtil.newStringBody(body, mediaType);
        return prepare(url, method, headers, requestBody);
    }

    @Override
    public Request prepare(String url, String method, Map<String, String> headers, byte[] body) {
        RequestBody requestBody = OkHttpUtil.newBytesBody(body);
        return prepare(url, method, headers, requestBody);
    }

    @Override
    public Response request(Request req) throws IOException {
        return client.newCall(req).execute();
    }

    @Override
    public void save(Response resp, String path) throws IOException {
        ResponseBody body = resp.body();
        if (body == null) throw new IOException("response body is null");

        OkHttpUtil.save(body, path);
    }

    @Override
    public String string(Response resp) throws IOException {
        ResponseBody body = resp.body();
        if (body == null) return null;
        else return body.string();
    }

    @Override
    public byte[] bytes(Response resp) throws IOException {
        ResponseBody body = resp.body();
        if (body == null) return null;
        else return body.bytes();
    }

    @Override
    public InputStream inputStream(Response resp) {
        ResponseBody body = resp.body();
        if (body == null) return null;
        else return body.byteStream();
    }

    @Override
    public Reader reader(Response resp) {
        ResponseBody body = resp.body();
        if (body == null) return null;
        else return body.charStream();
    }

    private Request prepare(String url, String method, Map<String, String> headers, RequestBody body) {
        method = method.toUpperCase();
        Request.Builder builder = new Request.Builder();
        String mediaType = null;
        if (ObjectUtil.isNotEmpty(headers)) {
            headers.forEach(builder::addHeader);
        }

        switch (method) {
            case "GET":
                return builder.get().build();
            case "HEAD":
                return builder.head().build();
        }

        switch (method) {
            case "POST":
                return builder.post(body).build();
            case "PUT":
                return builder.put(body).build();
            case "DELETE":
                return builder.delete(body).build();
            case "PATCH":
                return builder.patch(body).build();
        }
        return null;
    }

}
