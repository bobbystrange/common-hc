package org.dreamcat.common.hc.httpclient;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Test;

/**
 * Create by tuke on 2019-02-19
 */
@Slf4j
public class InterceptableHttpClientWgetTest {

    @Test
    public void test() {
        InterceptableHttpClientWget wget = new InterceptableHttpClientWget();

        wget.interceptors().add(chain -> {
            HttpUriRequest original = chain.original();
            log.info("Preparing uri {}", original.getURI());
            return chain.proceed(original);
        });
        wget.newCall(new HttpGet("localhost"));

    }
}
