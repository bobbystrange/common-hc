package org.dreamcat.common.hc.httpclient;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.dreamcat.common.core.chain.InterceptTarget;
import org.dreamcat.common.core.chain.Interceptor;
import org.dreamcat.common.core.chain.RealCall;
import org.dreamcat.common.core.chain.RealInterceptTarget;
import org.dreamcat.common.function.ThrowableFunction;

import java.util.List;

/**
 * Create by tuke on 2018/11/25
 */
public class AsyncHttpClientWget extends HttpClientWget implements InterceptTarget<HttpUriRequest, CloseableHttpResponse> {

    private final RealInterceptTarget<HttpUriRequest, CloseableHttpResponse> target;

    public AsyncHttpClientWget() {
        this(HttpClients.createDefault());
    }

    public AsyncHttpClientWget(CloseableHttpClient client) {
        super(client);
        this.target = RealInterceptTarget.builder(
                (ThrowableFunction<HttpUriRequest, CloseableHttpResponse>) this::request)
                .build();
    }

    @Override
    public List<Interceptor<HttpUriRequest, CloseableHttpResponse>> interceptors() {
        return target.interceptors();
    }

    @Override
    public Interceptor.Dispatcher<HttpUriRequest, CloseableHttpResponse> dispatcher() {
        return target.dispatcher();
    }

    @Override
    public Interceptor.Listener<HttpUriRequest, CloseableHttpResponse> listener() {
        return target.listener();
    }

    @Override
    public void requestAsync(HttpUriRequest request, Callback<HttpUriRequest, CloseableHttpResponse> callback) {
        target.newCall(request).enqueue(new Interceptor.Callback<HttpUriRequest, CloseableHttpResponse>() {
            @Override
            public void onComptele(RealCall<HttpUriRequest, CloseableHttpResponse> call, CloseableHttpResponse response) {
                callback.onComplete(call.original(), response);
            }

            @Override
            public void onError(RealCall<HttpUriRequest, CloseableHttpResponse> call, Exception e) {
                callback.onError(call.original(), e);
            }
        });
    }
}
