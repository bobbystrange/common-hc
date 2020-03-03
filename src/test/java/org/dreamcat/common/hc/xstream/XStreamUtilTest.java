package org.dreamcat.common.hc.xstream;

import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.bean.BeanUtil;
import org.dreamcat.common.hc.test.BeanBase;
import org.junit.Test;

/**
 * Create by tuke on 2019-02-16
 */
@Slf4j
public class XStreamUtilTest {

    private BeanBase obj = BeanBase.newInstance();

    @Test
    public void toXml() {
        String xml = XStreamUtil.toXML(obj);
        log.info("xml:\n{}", xml);

        BeanBase newObj = XStreamUtil.fromXML(xml, BeanBase.class);
        assert newObj != null;
        log.info("bean:\n{}", BeanUtil.toPrettyString(newObj));

    }
}
/*
<?xml version="1.0" ?>
<BeanBase>
    <name>1a7afa1842374e57b3d74814c6e54711</name>
    <random>0.2665323873175638</random>
    <nonce>731008779</nonce>
    <tab>
        <name>31CRZ7ULXG</name>
        <startTime>3652-10-13 16:00:00.0 UTC</startTime>
        <endTime>3900-01-31 15:59:59.0 UTC</endTime>
        <incr>1550282260784</incr>
    </tab>
    <extraMeta>
        <level>7</level>
        <name>java.lang.Object</name>
        <version>
            <majorVersion>0</majorVersion>
            <minorVersion>1</minorVersion>
            <microVersion>1</microVersion>
            <type>ALPHA</type>
            <specialVersion>0.1.1alpha</specialVersion>
        </version>
        <outer-class>
            <random>0.0</random>
            <nonce>-1992292813</nonce>
        </outer-class>
    </extraMeta>
</BeanBase>



 */
