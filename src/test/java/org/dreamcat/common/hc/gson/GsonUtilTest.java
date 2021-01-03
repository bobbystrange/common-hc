package org.dreamcat.common.hc.gson;

import static org.dreamcat.common.util.BeanUtil.pretty;
import static org.junit.Assert.assertNotNull;

import com.google.gson.JsonElement;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.hc.test.BeanBase;
import org.junit.Test;

/**
 * Create by tuke on 2019-02-16
 */
@Slf4j
public class GsonUtilTest {

    private final BeanBase obj = BeanBase.newInstance();

    @Test
    public void toXml() {
        String json = GsonUtil.toJson(obj);
        log.info("json:\n{}", json);

        BeanBase newObj = GsonUtil.fromJson(json, BeanBase.class);
        assertNotNull(newObj);
        log.info("bean:\n{}", pretty(newObj));

    }

    @Test
    public void toList() {
        String json = "['FleetWood', 'MAC']";
        log.info("json:\n{}", json);
        List<String> list = GsonUtil.toList(json);
        log.info("list:\n{}\n", pretty(list));

        json = "[1, '', true, [], {}]";
        List<Object> list2 = GsonUtil.toList(json);
        assertNotNull(list2);
        log.info("list2:\n{}", pretty(list2));
    }

    @Test
    public void toMap() {
        String json1 = "{\"john\": \"lennon\", \"bob\": \"dylan\"}";
        log.info("json:\n{}", json1);
        Map<String, Object> map1 = GsonUtil.toMap(json1);
        log.info("map1:\n{}\n", map1);

        String json2 = GsonUtil.toJson(obj);
        log.info("json:\n{}", json2);
        Map<String, Object> map2 = GsonUtil.toMap(json2);
        log.info("map2:\n{}\n", map2);

        assertNotNull(obj);
        log.info("obj:\n{}", pretty(obj));
        log.info("map3:\n{}", GsonUtil.toMap(obj));

    }

    @Test
    public void toJson() {
        String json = "{\"john\": \"lennon\", \"bob\": \"dylan\"}";
        JsonElement root = GsonUtil.toJsonElement(json);
        assertNotNull(root);
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
