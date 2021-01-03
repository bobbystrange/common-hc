package org.dreamcat.common.hc.okhttp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.xstream.XStream;
import java.io.IOException;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import org.dreamcat.common.hc.gson.GsonUtil;
import org.dreamcat.common.hc.xstream.XStreamUtil;
import org.dreamcat.common.hc.xstream.XStreamXmlConverterFactory;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Slf4j
public final class RetrofitUtil {

    private RetrofitUtil() {
    }

    private static final HashMap<String, Retrofit> instances = new HashMap<>();

    private static final Gson gson = new GsonBuilder().create();
    private static final Converter.Factory gsonFactory =
            GsonConverterFactory.create(gson);
    private static final XStreamXmlConverterFactory xstreamFactory;

    static {
        XStream xStream = XStreamUtil.newXStream();
        xstreamFactory = XStreamXmlConverterFactory.create(xStream);
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static <T> T createService(Retrofit retrofit, Class<T> clazz) {
        return retrofit.create(clazz);
    }

    /**
     * unwrap retrofit2.Call
     *
     * @param call Retrofit Call
     * @param <T>  wrapped body
     * @return unwrapped body, such as json/xml bean
     * @throws RuntimeException IO error, or response code is not 2xx
     */
    public static <T> T unwrap(Call<T> call) {
        try {
            Response<T> response = call.execute();
            if (response.isSuccessful()) {
                T responseBody = response.body();
                String responseContent;
                if (responseBody == null) {
                    responseContent = "null";
                } else if (responseBody instanceof ResponseBody) {
                    responseContent = ((ResponseBody) responseBody).string();
                } else {
                    responseContent = GsonUtil.toJson(responseBody);
                }
                log.info("<-- success:\t{}", responseContent);
                return responseBody;
            } else {
                log.info("<-- fail:\tcode={}, message={}", response.code(), response.message());
                String errorString =
                        response.errorBody() == null ? "null" : response.errorBody().string();
                throw new RuntimeException(errorString);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static Retrofit getInstance4Json(OkHttpClient client) {
        return getInstance4Json(null, client);
    }

    public static Retrofit getInstance4Json(String baseUrl, OkHttpClient client) {
        return getInstance(baseUrl, gsonFactory, client);
    }

    public static Retrofit getInstance4Xml(OkHttpClient client) {
        return getInstance4Xml(null, client);
    }

    public static Retrofit getInstance4Xml(String baseUrl, OkHttpClient client) {
        return getInstance(baseUrl, xstreamFactory, client);
    }

    public static Retrofit getInstance(String baseUrl, Converter.Factory factory,
            OkHttpClient client) {
        return getInstance(baseUrl, factory, null, client);
    }

    public static Retrofit getInstance(
            String baseUrl, Converter.Factory converterFactory,
            CallAdapter.Factory callAdapterFactory, OkHttpClient client) {
        if (instances.containsKey(baseUrl)) return instances.get(baseUrl);

        Retrofit.Builder builder = new Retrofit.Builder()
                .client(client);
        if (baseUrl != null) builder.baseUrl(baseUrl);
        if (converterFactory != null) builder.addConverterFactory(converterFactory);
        if (callAdapterFactory != null) builder.addCallAdapterFactory(callAdapterFactory);

        Retrofit retrofit = builder.build();
        instances.put(baseUrl, retrofit);
        return retrofit;
    }

}

