package com.tukeof.common.hc.httpclient;

import com.tukeof.common.core.chain.InterceptTarget;
import com.tukeof.common.core.chain.Interceptor;
import com.tukeof.common.core.chain.RealInterceptTarget;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.List;

/**
 * Create by tuke on 2018/11/25
 */
public class InterceptableHttpClientWget extends HttpClientWget implements InterceptTarget<HttpUriRequest, CloseableHttpResponse> {

    private RealInterceptTarget<HttpUriRequest, CloseableHttpResponse> target;

    public InterceptableHttpClientWget() {
        this(HttpClients.createDefault());
    }

    public InterceptableHttpClientWget(CloseableHttpClient client) {
        super(client);
        this.target = RealInterceptTarget.builder(this::request).build();
    }

    @Override
    public List<Interceptor<HttpUriRequest, CloseableHttpResponse>> interceptors() {
        return target.interceptors();
    }

    @Override
    public Interceptor.Dispatcher<HttpUriRequest, CloseableHttpResponse> dispatcher() {
        return target.dispatcher();
    }
}
