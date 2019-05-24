package org.dreamcat.common.hc.okhttp;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

public class GlobalRequestInterceptor implements Interceptor {

    private final Map<String, String> headers;

    public GlobalRequestInterceptor(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        Request.Builder builder = request.newBuilder();
        for (String name : headers.keySet()) {
            builder.addHeader(name, headers.get(name));
        }

        return chain.proceed(builder.build());
    }
}
