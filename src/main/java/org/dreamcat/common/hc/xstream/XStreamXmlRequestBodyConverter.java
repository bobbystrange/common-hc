package org.dreamcat.common.hc.xstream;

import com.thoughtworks.xstream.XStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Converter;

final class XStreamXmlRequestBodyConverter<T> implements Converter<T, RequestBody> {

    private static final MediaType MEDIA_TYPE = MediaType.parse("application/xml; charset=UTF-8");

    private final XStream xStream;

    XStreamXmlRequestBodyConverter(XStream xStream) {
        this.xStream = xStream;
    }

    @Override
    public RequestBody convert(T value) throws IOException {
        try (Buffer buffer = new Buffer()) {
            try (OutputStreamWriter osw = new OutputStreamWriter(
                    buffer.outputStream(), StandardCharsets.UTF_8)) {
                xStream.toXML(value, osw);
                osw.flush();
            }
            return RequestBody.create(buffer.readByteString(), MEDIA_TYPE);
        }
    }
}
