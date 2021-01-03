package org.dreamcat.common.hc.httpclient;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.Arrays;

/**
 * Create by tuke on 2018/11/25
 */
@Slf4j
public class HttpLoggingInterceptor implements HttpRequestInterceptor, HttpResponseInterceptor {
    @Override
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        log.info("\t> {}", request.getRequestLine());
        Arrays.stream(request.getAllHeaders()).forEach(header ->
                log.info("\t>{}: {}", header.getName(), header.getValue()));
        log.info("\t>");
    }

    @Override
    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
        log.info("\t<{}", response.getStatusLine());
        Arrays.stream(response.getAllHeaders()).forEach(header ->
                log.info("\t<{}: {}", header.getName(), header.getValue()));
        log.info("\t<");
    }
}
