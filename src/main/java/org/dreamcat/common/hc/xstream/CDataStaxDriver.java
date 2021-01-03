package org.dreamcat.common.hc.xstream;

import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.io.xml.StaxWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class CDataStaxDriver extends StaxDriver {

    public CDataStaxDriver() {
        super(new QNameMap(), new NoNameCoder());
    }

    @Override
    public StaxWriter createStaxWriter(
            XMLStreamWriter out,
            boolean writeStartEndDocument) throws XMLStreamException {
        return new StaxWriterImpl(
                getQnameMap(), out,
                writeStartEndDocument, isRepairingNamespace(), getNameCoder());
    }

}
