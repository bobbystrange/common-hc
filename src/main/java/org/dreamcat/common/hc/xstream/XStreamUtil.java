package org.dreamcat.common.hc.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class XStreamUtil {

    private XStreamUtil() {
    }

    private static final XStream xmlStream = newXStream();

    public static String toXML(Object object) {
        xmlStream.processAnnotations(object.getClass());
        return xmlStream.toXML(object);
    }

    public static <T> T fromXML(String xml, Class<T> clazz) {
        xmlStream.processAnnotations(clazz);

        try {
            T instance = clazz.newInstance();
            xmlStream.fromXML(xml, instance);
            return instance;
        } catch (Exception t) {
            log.error(t.getMessage(), t);
            return null;
        }
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static XStream newXStream() {
        XStream xmlStream = new XStream(new CDataStaxDriver());
        xmlStream.autodetectAnnotations(true);
        xmlStream.ignoreUnknownElements();
        return xmlStream;
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    /**
     * require `org.codehaus.jettison` in classpath
     */
    public static String toJson(Object object) {
        XStream xmlStream = new XStream(new JettisonMappedXmlDriver());
        return xmlStream.toXML(object);
    }
}
