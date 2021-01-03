package org.dreamcat.common.hc.okhttp;

import java.io.IOException;
import java.util.Map;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class GlobalRequestInterceptor implements Interceptor {

    private final Map<String, String> headers;

    public GlobalRequestInterceptor(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        Request.Builder builder = request.newBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String name = entry.getKey();
            builder.addHeader(name, headers.get(name));
        }

        return chain.proceed(builder.build());
    }
}
