package org.dreamcat.common.hc.xstream;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import lombok.extern.slf4j.Slf4j;

/**
 * Create by tuke on 2021/1/3
 */
@Slf4j
@SuppressWarnings("rawtypes")
class StaxWriterImpl extends StaxWriter {

    private boolean cdata = false;
    private boolean globalCdata = false;
    private Class targetClass;
    /**
     * same as the field `out` in {@link StaxWriter}
     */
    private final XMLStreamWriter writer;

    StaxWriterImpl(
            QNameMap qnameMap, XMLStreamWriter out,
            boolean writeEnclosingDocument, boolean namespaceRepairingMode,
            NameCoder nameCoder) throws XMLStreamException {
        super(qnameMap, out, writeEnclosingDocument, namespaceRepairingMode, nameCoder);
        this.writer = out;
    }

    @Override
    public void startNode(String name, Class clazz) {
        super.startNode(name);

        if ("xml".equals(name)) {
            targetClass = clazz;
            Annotation xStreamCDATA = clazz.getAnnotation(XStreamCDATA.class);
            if (xStreamCDATA instanceof XStreamCDATA) {
                globalCdata = true;
            }
            return;
        }

        cdata = false;

        if (targetClass == null) {
            return;
        }

        Field[] fields = targetClass.getDeclaredFields();
        for (Field field : fields) {
            XStreamAlias xStreamAlias = field.getAnnotation(XStreamAlias.class);
            String domName = xStreamAlias != null ? xStreamAlias.value() : field.getName();

            if (name.equals(domName)) {
                if (globalCdata) {
                    cdata = true;
                } else {
                    XStreamCDATA xStreamCDATA = field.getAnnotation(XStreamCDATA.class);
                    if (xStreamCDATA != null) {
                        cdata = true;
                    }
                }
                break;
            }
        }
    }


    @Override
    public void setValue(String value) {
        if (cdata) {
            try {
                writer.writeCData(value);
            } catch (XMLStreamException e) {
                log.error(e.getMessage(), e);
            }
        } else {
            super.setValue(value);
        }

    }

}
