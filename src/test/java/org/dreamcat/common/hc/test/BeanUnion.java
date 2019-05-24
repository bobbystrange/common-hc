package org.dreamcat.common.hc.test;

import org.dreamcat.common.util.RandomUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Create by tuke on 2019-02-12
 */
@Data
public class BeanUnion {
    private Sub1 sub1;
    private Sub2 sub2;
    private Sub3 sub3;

    @Data
    public static class Sub1 extends Key {
        private String name;
        private long amount;

        public Sub1(Date yearMonth, long partnerId, String name, long amount) {
            super(yearMonth, partnerId);
            this.name = name;
            this.amount = amount;
        }
    }

    @Data
    public static class Sub2 extends Key {
        private String name;
        private long amount;

        public Sub2(Date yearMonth, long partnerId, String name, long amount) {
            super(yearMonth, partnerId);
            this.name = name;
            this.amount = amount;
        }
    }

    @Data
    public static class Sub3 extends Key {
        private String name;
        private long amount;

        public Sub3(Date yearMonth, long partnerId, String name, long amount) {
            super(yearMonth, partnerId);
            this.name = name;
            this.amount = amount;
        }
    }

    @AllArgsConstructor
    @Data
    public static class Key {
        @Anno
        private Date yearMonth;
        @Anno
        private long partnerId;

        @Override
        public int hashCode() {
            int id = (int) partnerId;
            return Integer.parseInt(SDF.format(yearMonth)) + id * 10000_00;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (obj instanceof Key) {
                return hashCode() == obj.hashCode();
            }
            return false;
        }
    }

    public static BeanUnion newInstance() {
        BeanUnion obj = new BeanUnion();
        obj.setSub1(new Sub1(date1, partnerIds[0], RandomUtil.generateUuid32(), RANDOM.nextLong()));
        obj.setSub2(new Sub2(date2, partnerIds[1], RandomUtil.generateUuid32(), RANDOM.nextLong()));
        obj.setSub3(new Sub3(date3, partnerIds[2], RandomUtil.generateUuid32(), RANDOM.nextLong()));
        return obj;
    }

    public static List<Sub1> ofSub1() {
        List<Sub1> list = new ArrayList<>();
        for (long partnerId : partnerIds) {
            list.add(new Sub1(date1, partnerId, RandomUtil.generateUuid32(), RANDOM.nextLong()));
            list.add(new Sub1(date2, partnerId, RandomUtil.generateUuid32(), RANDOM.nextLong()));
            list.add(new Sub1(date3, partnerId, RandomUtil.generateUuid32(), RANDOM.nextLong()));
            list.add(new Sub1(date4, partnerId, RandomUtil.generateUuid32(), RANDOM.nextLong()));
        }
        return list;
    }

    public static List<Sub2> ofSub2() {
        List<Sub2> list = new ArrayList<>();
        for (long partnerId : partnerIds) {
            list.add(new Sub2(date1, partnerId, RandomUtil.generateUuid32(), RANDOM.nextLong()));
            list.add(new Sub2(date2, partnerId, RandomUtil.generateUuid32(), RANDOM.nextLong()));
            list.add(new Sub2(date3, partnerId, RandomUtil.generateUuid32(), RANDOM.nextLong()));
            list.add(new Sub2(date4, partnerId, RandomUtil.generateUuid32(), RANDOM.nextLong()));
        }
        return list;
    }

    public static List<Sub3> ofSub3() {
        List<Sub3> list = new ArrayList<>();
        for (long partnerId : partnerIds) {
            list.add(new Sub3(date1, partnerId, RandomUtil.generateUuid32(), RANDOM.nextLong()));
            list.add(new Sub3(date2, partnerId, RandomUtil.generateUuid32(), RANDOM.nextLong()));
            list.add(new Sub3(date3, partnerId, RandomUtil.generateUuid32(), RANDOM.nextLong()));
            list.add(new Sub3(date4, partnerId, RandomUtil.generateUuid32(), RANDOM.nextLong()));
        }
        return list;
    }

    private static final Date date1 = of(1999, 9, 1);
    private static final Date date2 = of(1999, 9, 2);
    private static final Date date3 = of(1999, 9, 14);
    private static final Date date4 = of(1999, 9, 15);
    private static final long[] partnerIds = new long[]{123, 456, 789, 1000};
    private static final Random RANDOM = new Random();
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMM");

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
    public @interface Anno {
    }

    static Date of(int y, int m, int d) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1752, 9, 1);
        return calendar.getTime();
    }
}
