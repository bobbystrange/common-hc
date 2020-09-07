package org.dreamcat.common.hc.gson;

import com.google.gson.JsonElement;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.bean.BeanFormatUtil;
import org.dreamcat.common.hc.test.BeanBase;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Create by tuke on 2019-02-16
 */
@Slf4j
public class GsonUtilTest {

    private final BeanBase obj = BeanBase.newInstance();
    String json;

    @Test
    public void toXml() {
        json = GsonUtil.toJson(obj);
        log.info("json:\n{}", json);

        BeanBase newObj = GsonUtil.fromJson(json, BeanBase.class);
        assert newObj != null;
        log.info("bean:\n{}", BeanFormatUtil.pretty(newObj));

    }

    @Test
    public void toList() {
        json = "['FleetWood', 'MAC']";
        log.info("json:\n{}", json);
        List<String> list = GsonUtil.toList(json);
        log.info("list:\n{}\n", BeanFormatUtil.pretty(list));

        json = "[1, '', true, [], {}]";
        List<Object> list2 = GsonUtil.toList(json);
        log.info("list2:\n{}", BeanFormatUtil.pretty(list2));
    }

    @Test
    public void toMap() {
        json = "{\"john\": \"lennon\", \"bob\": \"dylan\"}";
        log.info("json:\n{}", json);
        Map<String, Object> map = GsonUtil.toMap(json);
        log.info("map:\n{}\n", BeanFormatUtil.pretty(map));

        json = "{\"john\": \"lennon\", \"bob\": [\"dylan\"]}";
        log.info("json:\n{}", json);
        Map<String, Object> map2 = GsonUtil.toMap(json);
        log.info("map2:\n{}\n", BeanFormatUtil.pretty(map2));

        json = GsonUtil.toJson(obj);
        log.info("json:\n{}", json);
        Map<String, Object> map3 = GsonUtil.toMap(json);
        log.info("map:\n{}\n", BeanFormatUtil.pretty(map3));

    }

    @Test
    public void toJson() {
        json = "{\"john\": \"lennon\", \"bob\": \"dylan\"}";
        JsonElement root = GsonUtil.toJsonElement(json);
        log.info("{}", GsonUtil.toJson(root));

    }

}
/*
{
    "name": "e4d78bc74cb9443fb3014708d1e0351e",
    "random": 0.5533100508767517,
    "nonce": -2124098284,
    "tab": {
        "name": "W3QY8R4WXF",
        "startTime": 53103571200000,
        "endTime": 60907564799000,
        "incr": 1550283713295
    },
    "extraMeta": {
        "level": 1,
        "name": "java.lang.Object",
        "version": {
            "majorVersion": 0,
            "minorVersion": 1,
            "microVersion": 1,
            "type": "ALPHA",
            "specialVersion": "0.1.1alpha"
        }
    }
}
 */
