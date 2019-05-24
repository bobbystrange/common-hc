package org.dreamcat.common.hc.xstream;

import com.thoughtworks.xstream.XStream;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class XStreamXmlConverterFactory extends Converter.Factory {

    private final XStream xStream;

    private XStreamXmlConverterFactory(XStream xStream) {
        if (xStream == null) throw new NullPointerException("xStream == null");
        this.xStream = xStream;
    }

    /**
     * Create an instance using a default {@link com.thoughtworks.xstream.XStream} instance for conversion.
     * @return Converter.Factory
     */
    public static XStreamXmlConverterFactory create() {
        return create(new XStream());
    }

    /**
     * Create an instance using {@code xStream} for conversion.
     * @param xStream custom {@link com.thoughtworks.xstream.XStream} instance for conversion.
     * @return Converter.Factory
     */
    public static XStreamXmlConverterFactory create(XStream xStream) {
        return new XStreamXmlConverterFactory(xStream);
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {

        if (!(type instanceof Class)) {
            return null;
        }

        Class<?> cls = (Class<?>) type;

        return new XStreamXmlResponseBodyConverter<>(cls, xStream);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {

        if (!(type instanceof Class)) {
            return null;
        }

        return new XStreamXmlRequestBodyConverter<>(xStream);
    }
}
