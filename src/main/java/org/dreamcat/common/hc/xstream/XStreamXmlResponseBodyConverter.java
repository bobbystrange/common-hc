package org.dreamcat.common.hc.xstream;

import com.thoughtworks.xstream.XStream;
import okhttp3.ResponseBody;
import retrofit2.Converter;

final class XStreamXmlResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private final Class<T> cls;
    private final XStream xStream;

    XStreamXmlResponseBodyConverter(Class<T> cls, XStream xStream) {
        this.cls = cls;
        this.xStream = xStream;
    }

    @Override
    public T convert(ResponseBody value) {
        try {
            this.xStream.processAnnotations(cls);
            T instance = cls.newInstance();
            this.xStream.fromXML(value.byteStream(), instance);
            return instance;

        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        } finally {
            value.close();
        }
    }
}
