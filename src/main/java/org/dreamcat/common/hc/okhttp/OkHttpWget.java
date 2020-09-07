package org.dreamcat.common.hc.okhttp;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.dreamcat.common.core.Wget;
import org.dreamcat.common.util.ObjectUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;

@Slf4j
public class OkHttpWget implements Wget<Request, Response> {
    private final OkHttpClient client;

    public OkHttpWget() {
        this(false);
    }

    public OkHttpWget(boolean logging) {
        this(logging ? OkHttpUtil.newClient(OkHttpUtil.newHttpLoggingInterceptor())
                : OkHttpUtil.newClient());
    }

    public OkHttpWget(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public Request prepare(String url, String method, Map<String, String> headers) {
        return prepare(url, method, headers, OkHttpUtil.EMPTY_BODY);
    }

    @Override
    public Request prepare(String url, String method, Map<String, String> headers, String body, String contentType) {
        RequestBody requestBody = OkHttpUtil.newStringBody(body, contentType);
        return prepare(url, method, headers, requestBody);
    }

    @Override
    public Request prepare(String url, String method, Map<String, String> headers, byte[] body, String contentType) {
        RequestBody requestBody = OkHttpUtil.newBytesBody(body, contentType);
        return prepare(url, method, headers, requestBody);
    }

    @Override
    public Response request(Request request) throws IOException {
        return client.newCall(request).execute();
    }

    @Override
    public String string(Response response) throws IOException {
        ResponseBody body = response.body();
        if (body == null) return null;
        else return body.string();
    }

    @Override
    public byte[] bytes(Response response) throws IOException {
        ResponseBody body = response.body();
        if (body == null) return null;
        else return body.bytes();
    }

    @Override
    public InputStream inputStream(Response response) {
        ResponseBody body = response.body();
        if (body == null) return null;
        else return body.byteStream();
    }

    @Override
    public boolean saveTo(Response response, File dir) throws IOException {
        // attachment; filename="xxx"
        String disposition = response.header("Content-Disposition");
        if (ObjectUtil.isEmpty(disposition)) {
            log.warn("HTTP Header Content-Disposition is not found");
            return false;
        }

        String[] elements = disposition.split(";");
        if (ObjectUtil.isEmpty(elements)) {
            log.warn("HTTP Header Content-Disposition has no elements");
        }

        for (String element : elements) {
            element = element.trim();
            if (element.length() > "filename=\"\"".length() && element.startsWith("filename=\"") && element.endsWith("\"")) {
                String filename = element.substring("filename=\"".length(), element.length() - 1);
                return save(response, new File(dir, filename));
            }
        }

        log.warn("HTTP Header Content-Disposition has no field called filename");
        return false;
    }

    @Override
    public Reader reader(Response response) {
        ResponseBody body = response.body();
        if (body == null) return null;
        else return body.charStream();
    }

    @Override
    public void requestAsync(Request request, Callback<Request, Response> callback) {
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onError(call.request(), e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                callback.onComplete(call.request(), response);
            }
        });
    }

    @Override
    public Response postForm(String url, Map<String, String> headers, Map<String, String> form) throws IOException {
        return request(prepare(url, "POST", headers, OkHttpUtil.newFormBody(form)));
    }

    @Override
    public Response postFormData(String url, Map<String, String> headers, Map<String, Object> formData) throws IOException {
        return request(prepare(url, "POST", headers, OkHttpUtil.newMultipartBody(formData)));
    }

    private Request prepare(String url, String method, Map<String, String> headers, RequestBody body) {
        method = method.toUpperCase();
        Request.Builder builder = new Request.Builder()
                .url(url);
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
        throw new IllegalArgumentException("No such HTTP method: " + method);
    }

}
