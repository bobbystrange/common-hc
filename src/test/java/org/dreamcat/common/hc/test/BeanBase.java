package org.dreamcat.common.hc.test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.bean.BeanUtil;
import org.dreamcat.common.util.RandomUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Calendar;
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
    private final int nonce = new Random().nextInt();
    protected Table tab;
    @Anno
    private String name;
    private transient Date timestamp;
    private volatile double random;
    @Anno
    private Meta extraMeta;

    public static BeanBase newInstance() {
        BeanBase obj = new BeanBase();
        obj.setName(RandomUtil.uuid());
        obj.setTimestamp(new Date());
        obj.setRandom(Math.random());
        obj.setTab(new BeanBase.Table(
                RandomUtil.choose36(10),
                new Date(1752, Calendar.OCTOBER, 14, 0, 0, 0),
                new Date(1999, Calendar.DECEMBER, 31, 23, 59, 59),
                new Date().getTime()));

        BeanBase.Meta meta = obj.new Meta();
        meta.setLevel((int) (Math.random() * 8));
        meta.setName(obj.getClass().getName());
        meta.setVersion(new Version(0, 1, 0, Version.Type.RELEASE));

        BeanBase obj2 = new BeanBase();
        BeanBase.Meta meta2 = obj2.new Meta();
        meta2.setLevel((int) (Math.random() * 8));
        meta2.setName(obj.getClass().getSuperclass().getName());
        meta2.setVersion(new Version(0, 1, 1, Version.Type.ALPHA));
        obj.setExtraMeta(meta2);
        log.info("\n{}", BeanUtil.toPrettyString(obj));
        return obj;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
    public @interface Anno {
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

    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public class Meta {
        private int level;
        private String name;
        private Version version;
    }

}
