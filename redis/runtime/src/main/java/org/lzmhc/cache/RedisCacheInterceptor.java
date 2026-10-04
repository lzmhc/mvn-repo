package org.lzmhc.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.lzmhc.service.ReactiveRedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * AOP拦截器
 */
@RedisCache
@Interceptor
@Priority(Interceptor.Priority.APPLICATION + 1)
public class RedisCacheInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(RedisCacheInterceptor.class);

    @Inject
    ReactiveRedisService redisService;
    // 注入 Jackson 的 ObjectMapper
    @Inject
    ObjectMapper objectMapper;

    //环绕拦截
    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        // 获取原方法
        Method method = context.getMethod();
        // 获取注解实例
        RedisCache redisCache = method.getAnnotation(RedisCache.class);
        // 生成key
        String redisKey = generateRedisKey(context, redisCache);
        // 获取方法的实际返回类型
        Class<?> returnType = getMethodReturnType(method);
        // 尝试读取redis
        Uni<Object> resultUni = redisService.get(redisKey)
                .onItem().transform(cachedValue -> {
                    if(cachedValue != null) {
                        // 缓存命中
                        try{
                            return deserialize(cachedValue, returnType);
                        }catch (Exception e){
                            LOG.error("Failed to deserizlize cached value for key: {}", redisKey, e);
                        }
                    }
                    return null;
                })
                .onItem().ifNull().switchTo(() -> {
                    Uni<Object> methodResultUni = Uni.createFrom().item(() -> {
                        try{
                            // 缓存未命中，执行原方法
                            return context.proceed();
                        }catch (Exception e){
                            throw new RuntimeException(e);
                        }
                    }).runSubscriptionOn(Infrastructure.getDefaultWorkerPool());

                    return methodResultUni
                            .onItem().transformToUni(methodResult -> {
                                if (methodResult instanceof Uni) {
                                    return ((Uni<?>) methodResult)
                                            .onItem().transform(item -> {
                                                cacheResult(redisKey, item, redisCache);
                                                return item;
                                            });
                                } else {
                                    cacheResult(redisKey, methodResult, redisCache);
                                    return Uni.createFrom().item(methodResult);
                                }
                            });
                });
        // 兼容处理响应式和阻塞式
        if (Uni.class.isAssignableFrom(method.getReturnType())) {
            return resultUni;
        } else {
            return resultUni.await().indefinitely();
        }
    }
    // 获取方法返回值
    private Class<?> getMethodReturnType(Method method) {
        Class<?> returnType = method.getReturnType();
        if (Uni.class.isAssignableFrom(returnType)) {
            try {
                return (Class<?>) ((java.lang.reflect.ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
            } catch (ClassCastException e) {
                LOG.warn("Could not determine generic type of Uni return type for method {}. Using Object.class.", method.getName());
                return Object.class;
            }
        }
        return returnType;
    }

    // 生成key
    private String generateRedisKey(InvocationContext context, RedisCache redisCache) {
        Method method = context.getMethod();
        Object[] parameters = context.getParameters();

        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(method.getDeclaringClass().getSimpleName())
                .append(":")
                .append(method.getName())
                .append(":");
        if (parameters != null) {
            for (Object param : parameters) {
                keyBuilder.append(param != null ? param.toString() : "null").append(":");
            }
        }
        return keyBuilder.toString();
    }

    //序列化
    private String serialize(Object value) throws JsonProcessingException {
        return objectMapper.writeValueAsString(value);
    }
    // 反序列化
    private Object deserialize(String str, Class<?> returnType) throws JsonProcessingException {
        if ("REDIS_NULL".equals(str)) {
            return null;
        }
        return objectMapper.readValue(str, returnType);
    }

    // 缓存
    private void cacheResult(String redisKey, Object result, RedisCache redisCache) {
        String serializedValue ;
        long ttlSeconds;
        try {
            if (result == null) {
                serializedValue = "REDIS_NULL";
                ttlSeconds = 60;
            }else{
                serializedValue = serialize(result);
                ttlSeconds = redisCache.timeUnit().toSeconds(redisCache.ttl());
            }
            if(ttlSeconds > 0) {
                redisService.setex(redisKey, serializedValue, ttlSeconds).subscribe().with(
                                success -> LOG.debug("Successfully cached with TTL for key: {}", redisKey),
                                failure -> LOG.error("Failed to cache with TTL for key: {}", redisKey, failure)
                        );
            }else{
                redisService.set(redisKey, serializedValue).subscribe().with(
                                success -> LOG.debug("Successfully cached with TTL for key: {}", redisKey),
                                failure -> LOG.error("Failed to cache with TTL for key: {}", redisKey, failure)
                        );
            }
        } catch (JsonProcessingException e) {
            LOG.error("Failed to serialize result for key: {}", redisKey, e);
        }
    }

//base64序列换缓存
//    // 环绕拦截
//    @AroundInvoke
//    public Object intercept(InvocationContext context) throws Exception {
//        // 获取被拦截的方法
//        Method method = context.getMethod();
//        // 获取注解实例
//        RedisCache cacheAnnotation = method.getAnnotation(RedisCache.class);
//        // 生成key
//        String redisKey = generateRedisKey(context, cacheAnnotation);
//
//        Uni<Object> resultUni = redisService.get(redisKey)
//                .onItem().transform(cachedValue -> {
//                    // 从redis缓存读取
//                    if (cachedValue != null) {
//                        try {
//                            // 缓存命中，反序列化返回
//                            return deserialize(cachedValue);
//                        } catch (IOException | ClassNotFoundException e) {
//                            return null;
//                        }
//                    }
//                    return null;
//                })
//                .onItem().ifNull().switchTo(() -> {
//                    // redis没有缓存
//                    // 将阻塞的方法调用包装在 Uni 中，并切换到工作线程池
//                    Uni<Object> methodResultUni = Uni.createFrom().item(() -> {
//                        try {
//                            // 调用原方法
//                            return context.proceed();
//                        } catch (Exception e) {
//                            throw new RuntimeException(e);
//                        }
//                    }).runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
//
//                    return methodResultUni
//                            .onItem().transformToUni(methodResult -> {
//                                // 如果原始方法返回的是一个 Uni
//                                if (methodResult instanceof Uni) {
//                                    return ((Uni<?>) methodResult)
//                                            .onItem().transform(item -> {
//                                                cacheResult(redisKey, item, cacheAnnotation);
//                                                return item;
//                                            });
//                                } else {
//                                    cacheResult(redisKey, methodResult, cacheAnnotation);
//                                    return Uni.createFrom().item(methodResult);
//                                }
//                            });
//                });
//        // 兼容处理 拦截器必须返回与原始方法声明类型相匹配的值
//        Class<?> returnType = method.getReturnType();
//        if (Uni.class.isAssignableFrom(returnType)) {
//            return resultUni;
//        } else {
//            return resultUni.await().indefinitely();
//        }
//    }
//
//    private String generateRedisKey(InvocationContext context, RedisCache cacheAnnotation) {
//
//        Method method = context.getMethod();
//        Object[] parameters = context.getParameters();
//
//        StringBuilder keyBuilder = new StringBuilder();
//        keyBuilder.append(method.getDeclaringClass().getSimpleName())
//                .append(":")
//                .append(method.getName())
//                .append(":");
//
//        if (parameters != null) {
//            for (Object param : parameters) {
//                keyBuilder.append(param != null ? param.toString() : "null").append(":");
//            }
//        }
//
//        return keyBuilder.toString();
//    }
//
//    // 缓存结果
//    private void cacheResult(String redisKey, Object result, RedisCache cacheAnnotation) {
//        if (result == null) {
//            return;
//        }
//
//        try {
//            // 序列化对象为 Base64 字符串
//            String serializedValue = serialize(result);
//
//            // 根据 TTL 设置缓存
//            if (cacheAnnotation.ttl() > 0) {
//                long ttlSeconds = cacheAnnotation.timeUnit().toSeconds(cacheAnnotation.ttl());
//                redisService.setex(redisKey, serializedValue, ttlSeconds)
//                        .subscribe().with(
//                                success -> LOG.debug("Successfully cached with TTL for key: {}", redisKey),
//                                failure -> LOG.error("Failed to cache with TTL for key: {}", redisKey, failure)
//                        );
//            } else {
//                redisService.set(redisKey, serializedValue)
//                        .subscribe().with(
//                                success -> LOG.debug("Successfully cached without TTL for key: {}", redisKey),
//                                failure -> LOG.error("Failed to cache without TTL for key: {}", redisKey, failure)
//                        );
//            }
//        } catch (IOException e) {
//            LOG.error("Failed to serialize result for key: {}", redisKey, e);
//        }
//    }
//
//    // base64序列化
//    private String serialize(Object obj) throws IOException {
//        if (obj instanceof String) {
//            return (String) obj;
//        }
//        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
//             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
//            oos.writeObject(obj);
//            return Base64.getEncoder().encodeToString(baos.toByteArray());
//        }
//    }
//    // base64反序列化
//    private Object deserialize(String str) throws IOException, ClassNotFoundException {
//        // 尝试直接作为字符串返回
//        try {
//            // 简单的检查，如果不是 Base64 编码，则认为是普通字符串
//            byte[] decoded = Base64.getDecoder().decode(str);
//            try (ByteArrayInputStream bais = new ByteArrayInputStream(decoded);
//                 ObjectInputStream ois = new ObjectInputStream(bais)) {
//                return ois.readObject();
//            }
//        } catch (IllegalArgumentException e) {
//            // 如果解码失败，说明它可能就是一个普通字符串
//            return str;
//        }
//    }
}
