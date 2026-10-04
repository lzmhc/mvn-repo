package org.lzmhc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于生成19位雪花ID
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Snowflake {
    /**
     * 数据中心ID (可选)
     * @return 数据中心ID
     */
    int dataCenterId() default -1; // -1 表示使用默认逻辑

    /**
     * 机器ID (可选)
     * @return 机器ID
     */
    int workerId() default -1; // -1 表示使用默认逻辑
}
