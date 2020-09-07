package org.dreamcat.common.hc.httpclient;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Test;

/**
 * Create by tuke on 2019-02-19
 */
@Slf4j
public class AsyncHttpClientWgetTest {

    @Test
    public void test() {
        AsyncHttpClientWget wget = new AsyncHttpClientWget();

        wget.interceptors().add(chain -> {
            HttpUriRequest original = chain.original();
            log.info("Preparing uri {}", original.getURI());
            return chain.proceed(original);
        });
        wget.newCall(new HttpGet("localhost"));

    }
}
