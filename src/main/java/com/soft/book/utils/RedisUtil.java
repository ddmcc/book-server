package com.soft.book.utils;

import com.qianxinyao.analysis.jieba.keyword.Keyword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author ddmcc
 */
@Component(value = "redisUtil")
public final class RedisUtil<K, V> {

    @Autowired
    private RedisTemplate<K, V> template;


    public V get(K key) {
        return template.opsForValue().get(key);
    }

    public V getAndSet(K key, V newValue) {
        return template.opsForValue().getAndSet(key, newValue);
    }

    public String get(K key, long start, long end) {
        return template.opsForValue().get(key, start, end);
    }

    public void set(K key, V value) {
        template.opsForValue().set(key, value);
    }

    public void set(K key, V value, final long timeout, final TimeUnit unit) {
        template.opsForValue().set(key, value, timeout, unit);
    }

    public void set(K key, V value, long offset) {
        template.opsForValue().set(key, value, offset);
    }

    public void remove(K key) {
        template.delete(key);
    }

    public void setSet(K k, List<Keyword> list) {
        list.forEach(e -> template.opsForZSet().add(k, (V)e.getName(), e.getTfidfvalue()));
    }

    public Set<V> getSet(K k) {
        return template.opsForZSet().reverseRangeByScore(k, 0, 99999999999999d);
    }

    public void removeSet(K key) {
        template.opsForZSet().removeRangeByScore(key, 0, 99999999999999d);
    }
}
