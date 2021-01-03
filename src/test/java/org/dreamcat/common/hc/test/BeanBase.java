package org.dreamcat.common.hc.test;

import com.google.gson.annotations.SerializedName;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Date;
import java.util.Random;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.util.RandomUtil;

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
    @Anno
    @SerializedName("name_str")
    private String name;
    private transient Date timestamp;
    private volatile double random;

    public static BeanBase newInstance() {
        BeanBase obj = new BeanBase();
        obj.setName(RandomUtil.uuid());
        obj.setTimestamp(new Date());
        obj.setRandom(Math.random());
        return obj;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER,
            ElementType.LOCAL_VARIABLE})
    public @interface Anno {

    }
}
