package org.lzmhc.service;

import io.smallrye.mutiny.Uni;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.Response;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
// redis工具类
@ApplicationScoped
public class ReactiveRedisService {
    private static final Logger LOG = LoggerFactory.getLogger(ReactiveRedisService.class);
    private final RedisAPI redisAPI;
    @Inject
    public ReactiveRedisService(RedisAPI redisAPI) {
        this.redisAPI = redisAPI;
    }

    // 封装通用响应处理
    private Uni<String> executeString(io.vertx.core.Future<Response> future) {
        return Uni.createFrom().emitter(emitter -> {
            future.onComplete(ar -> {
                if (ar.succeeded()) {
                    Response resp = ar.result();
                    String result = resp != null ? resp.toString() : null;
                    emitter.complete(result);
                } else {
                    LOG.error("Redis command failed", ar.cause());
                    emitter.fail(ar.cause());
                }
            });
        });
    }

    private Uni<Long> executeLong(io.vertx.core.Future<Response> future) {
        return Uni.createFrom().emitter(emitter -> {
            future.onComplete(ar -> {
                if (ar.succeeded()) {
                    Response resp = ar.result();
                    Long result = resp != null ? resp.toLong() : 0L;
                    emitter.complete(result);
                } else {
                    LOG.error("Redis command failed", ar.cause());
                    emitter.fail(ar.cause());
                }
            });
        });
    }

    private Uni<Boolean> executeBoolean(io.vertx.core.Future<Response> future) {
        return Uni.createFrom().emitter(emitter -> {
            future.onComplete(ar -> {
                if (ar.succeeded()) {
                    Response resp = ar.result();
                    boolean exists = resp != null && resp.toInteger() == 1;
                    emitter.complete(exists);
                } else {
                    LOG.error("Redis command failed", ar.cause());
                    emitter.fail(ar.cause());
                }
            });
        });
    }

    // ====== String 操作 ======

    public Uni<String> set(String key, String value) {
        return executeString(redisAPI.set(List.of(key, value)));
    }

    public Uni<String> setex(String key, String value, long ttlSeconds) {
        return executeString(redisAPI.set(List.of(key, value, "EX", String.valueOf(ttlSeconds))));
    }

    public Uni<String> setnx(String key, String value) {
        return executeString(redisAPI.set(List.of(key, value, "NX")));
    }

    public Uni<String> get(String key) {
        return executeString(redisAPI.get(key));
    }

    public Uni<Long> del(String key) {
        return executeLong(redisAPI.del(List.of(key)));
    }

    public Uni<Long> del(List<String> keys) {
        return executeLong(redisAPI.del(keys));
    }

    public Uni<Long> expire(String key, long seconds) {
        return executeLong(redisAPI.expire(List.of(key, String.valueOf(seconds))));
    }

    public Uni<Long> ttl(String key) {
        return executeLong(redisAPI.ttl(key));
    }

    public Uni<Boolean> exists(String key) {
        return executeBoolean(redisAPI.exists(List.of(key)));
    }

    // ====== Hash 操作 ======

    public Uni<String> hset(String key, String field, String value) {
        return executeString(redisAPI.hset(List.of(key, field, value)));
    }

    public Uni<String> hget(String key, String field) {
        return executeString(redisAPI.hget(key, field));
    }

    public Uni<Long> hdel(String key, String field) {
        return executeLong(redisAPI.hdel(List.of(key, field)));
    }

    // ====== List 操作 ======

    public Uni<Long> lpush(String key) {
        return executeLong(redisAPI.lpush(List.of(key)));
    }

    public Uni<String> rpop(String key) {
        List<String> args = java.util.Arrays.asList(key);
        args.add(0, key);
        return executeString(redisAPI.rpop(args));
    }

    // ====== 其他实用方法 ======

    public Uni<String> get(Long key) {
        return get(String.valueOf(key));
    }

    public Uni<String> set(Long key, String value) {
        return set(String.valueOf(key), value);
    }

    public Uni<String> setex(Long key, String value, Long ttl) {
        return setex(String.valueOf(key), value, ttl);
    }

    public Uni<String> setnx(Long key, String value) {
        return setnx(String.valueOf(key), value);
    }

    public Uni<Long> del(Long key) {
        return del(String.valueOf(key));
    }

//    public Uni<String> set(List<String> args) {
//        return Uni.createFrom().emitter(emitter -> {
//            redisAPI.set(args).onComplete(ar -> {
//                if (ar.succeeded()) {
//                    Response response = ar.result();
//                    emitter.complete(response != null ? response.toString() : null);
//                } else {
//                    emitter.fail(ar.cause());
//                }
//            });
//        });
//    }
//    public Uni<String> get(String key) {
//        return Uni.createFrom().emitter(emitter -> {
//            redisAPI.get(key).onComplete(ar -> {
//                if(ar.succeeded()){
//                    Response response = ar.result();
//                    emitter.complete(response != null ? response.toString() : null);
//                }else {
//                    emitter.fail(ar.cause());
//                }
//            });
//        });
//    }
//    public Uni<String> del(List<String> keys) {
//        return Uni.createFrom().emitter(emitter -> {
//            redisAPI.del(keys).onComplete(ar -> {
//                if(ar.succeeded()){
//                    Response response = ar.result();
//                    emitter.complete(response != null ? response.toString() : null);
//                }else {
//                    emitter.fail(ar.cause());
//                }
//            });
//        });
//    }
//    //set字符串
//    public Uni<String> set(String key, String value){
//        return this.set(List.of(key, value));
//    }
//    public Uni<String> set(Long key, String value){
//        return this.set(key.toString(), value);
//    }
//    //带过期时间
//    public Uni<String> setex(String key, String value, Long ttl) {
//        return set(List.of(key, value, "EX", String.valueOf(ttl)));
//    }
//    public Uni<String> setex(Long key, String value, Long ttl) {
//        return set(List.of(key+"", value, "EX", String.valueOf(ttl)));
//    }
//    //当不存在时set
//    public Uni<String> setnx(String key, String value) {
//        return set(List.of(key, value, "NX"));
//    }
//    //get字符串
//    public Uni<String> get(Long key) {
//        return this.get(key);
//    }
//    //del
//    public Uni<String> del(String key) {
//        return this.del(List.of(key));
//    }
}