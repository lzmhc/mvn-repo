package org.lzmhc.cache;

import jakarta.enterprise.util.Nonbinding;
import jakarta.interceptor.InterceptorBinding;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * redis自定义缓存注解
 */
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RedisCache {
    /**
     * SpEL 表达式，用于动态生成 Redis 的 key。
     */
    @Nonbinding
    String key() default "";
    /**
     * 缓存的过期时间。
     */
    @Nonbinding
    long ttl() default 60*15;
    /**
     * 过期时间的单位，默认为秒。
     */
    @Nonbinding
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
