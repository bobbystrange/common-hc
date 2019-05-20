package com.tukeof.common.hc.xstream;

import com.thoughtworks.xstream.XStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XStreamUtil {

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
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            return null;
        }
    }

    // require org.codehaus.jettison in classpath
//    public static String toJson(Object object) {
//        XStream xmlStream = new XStream(new JettisonMappedXmlDriver());
//        return xmlStream.toXML(object);
//    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    private static final XStream xmlStream = newXStream();

    public static XStream newXStream() {
        XStream xmlStream = new XStream(new CDataStaxDriver());
        xmlStream.autodetectAnnotations(true);
        xmlStream.ignoreUnknownElements();
        return xmlStream;
    }

}
