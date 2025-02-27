package com.whoj.whojbackendvalidationservice.service;

public interface RedisService {

    Object get(String key);

    Object pop(String key);

    void set(String key, String value, long time);

    Long increment(String key, long number);

    Double increment(String key, double number);
}
