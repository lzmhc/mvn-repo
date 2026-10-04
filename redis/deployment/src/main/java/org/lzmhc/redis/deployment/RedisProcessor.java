package org.lzmhc.redis.deployment;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import org.lzmhc.cache.RedisCacheInterceptor;
import org.lzmhc.service.ReactiveRedisService;

public class RedisProcessor {

    @BuildStep
    AdditionalBeanBuildItem registerBeans() {
        return AdditionalBeanBuildItem.builder()
                .addBeanClasses(
                        ReactiveRedisService.class,
                        RedisCacheInterceptor.class
                )
                .setUnremovable()
                .build();
    }
}
