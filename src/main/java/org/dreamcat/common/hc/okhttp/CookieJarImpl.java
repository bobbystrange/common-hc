package org.dreamcat.common.hc.okhttp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

@Slf4j
public class CookieJarImpl implements CookieJar {

    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    private final List<Cookie> emptyCookies = new ArrayList<>();

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        cookieStore.put(url.host(), cookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookies = cookieStore.get(url.host());
        if (cookies != null) {
            return cookies;
        } else {
            return emptyCookies;
        }
    }
}
