package org.dreamcat.common.hc.okhttp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.xstream.XStream;
import org.dreamcat.common.annotation.Nullable;
import org.dreamcat.common.hc.gson.GsonUtil;
import org.dreamcat.common.hc.xstream.XStreamUtil;
import org.dreamcat.common.hc.xstream.XStreamXmlConverterFactory;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RetrofitUtil {

    private static HashMap<String, Retrofit> instances = new HashMap<>();

    private static Gson gson = new GsonBuilder().create();
    private static Converter.Factory gsonFactory =
            GsonConverterFactory.create(gson);
    private static XStreamXmlConverterFactory xstreamFactory;

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
     * @param call Retrofit Call
     * @param <T> wrapped body
     * @return unwrapped body, such as json/xml bean
     * @throws RuntimeException IO error, or response code is not 2xx
     */
    @Nullable
    public static <T> T unwrap(Call<T> call) {
        try {
            Response<T> response = call.execute();
            if (response.isSuccessful()) {
                T responseBody = response.body();
                String responseContent;
                if (responseBody == null){
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
                String errorString = response.errorBody() == null ? "null" : response.errorBody().string();
                throw new RuntimeException(errorString);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static Retrofit getInstance4Json(String baseUrl) {
        return getInstance(baseUrl, gsonFactory);
    }

    public static Retrofit getInstance4Json(String baseUrl, Map<String, String> headers) {
        return getInstance(baseUrl, gsonFactory, headers);
    }

    public static Retrofit getInstance4Xml(String baseUrl) {
        return getInstance(baseUrl, xstreamFactory);
    }

    public static Retrofit getInstance4Xml(String baseUrl, Map<String, String> headers) {
        return getInstance(baseUrl, xstreamFactory, headers);
    }

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

    public static Retrofit getInstance(String baseUrl, Converter.Factory factory) {
        return getInstance(baseUrl, factory, null, null);
    }

    public static Retrofit getInstance(String baseUrl, Converter.Factory factory, Map<String, String> headers) {
        return getInstance(baseUrl, factory, null, headers);
    }

    public static Retrofit getInstance(String baseUrl, Converter.Factory converterFactory, CallAdapter.Factory callAdapterFactory) {
        return getInstance(baseUrl, converterFactory, callAdapterFactory, null);
    }

    public static Retrofit getInstance(
            String baseUrl, Converter.Factory converterFactory, CallAdapter.Factory callAdapterFactory, Map<String, String> headers) {
        if (instances.containsKey(baseUrl)) return instances.get(baseUrl);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(OkHttpUtil.newClient(headers, null, null));

        if (converterFactory != null) builder.addConverterFactory(converterFactory);
        if (callAdapterFactory != null) builder.addCallAdapterFactory(callAdapterFactory);

        Retrofit retrofit = builder.build();
        instances.put(baseUrl, retrofit);
        return retrofit;
    }

}

