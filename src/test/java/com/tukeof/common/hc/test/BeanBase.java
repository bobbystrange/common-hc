package com.tukeof.common.hc.test;

import com.tukeof.common.util.RandomUtil;
import com.tukeof.common.util.bean.BeanStringUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Date;
import java.util.Random;

/**
 * Create by tuke on 2019-02-12
 */
@Slf4j
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BeanBase {
    @Anno
    private String name;
    private transient Date timestamp;
    private volatile double random;
    private final int nonce = new Random().nextInt();
    protected Table tab;
    @Anno
    private Meta extraMeta;

    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public class Meta {
        private int level;
        private String name;
        private Version version;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static class Table {
        private String name;
        private Date startTime;
        private Date endTime;
        private long incr;
    }

    public static BeanBase newInstance(){
        BeanBase obj = new BeanBase();
        obj.setName(RandomUtil.generateUuid32());
        obj.setTimestamp(new Date());
        obj.setRandom(Math.random());
        obj.setTab(new BeanBase.Table(
                RandomUtil.nonce32(10),
                new Date(1752, 9, 14, 0, 0, 0),
                new Date(1999, 12, 31, 23, 59, 59),
                new Date().getTime()));

        BeanBase.Meta meta = obj.new Meta();
        meta.setLevel((int)(Math.random() * 8));
        meta.setName(obj.getClass().getName());
        meta.setVersion(new Version(0, 1, 0, Version.Type.RELEASE));

        BeanBase obj2 = new BeanBase();
        BeanBase.Meta meta2 = obj2.new Meta();
        meta2.setLevel((int)(Math.random() * 8));
        meta2.setName(obj.getClass().getSuperclass().getName());
        meta2.setVersion(new Version(0, 1, 1, Version.Type.ALPHA));
        obj.setExtraMeta(meta2);
        log.info("\n{}", BeanStringUtil.toPrettyString(obj));
        return obj;
    }


    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
    public @interface Anno{}

}