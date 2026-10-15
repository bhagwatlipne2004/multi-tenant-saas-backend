package com.bhagwat.saas.service;

public interface RedisService {

    void set(String key, String value);

    String get(String key);

    void delete(String key);
}