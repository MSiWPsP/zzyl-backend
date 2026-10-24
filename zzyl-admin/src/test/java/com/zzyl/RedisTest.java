package com.zzyl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataRedisTest
@EnabledIfEnvironmentVariable(named = "ZZYL_REDIS_TEST", matches = "true")
public class RedisTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void testString(){
        String key = "redis-test:" + UUID.randomUUID();
        try {
            redisTemplate.opsForValue().set(key, "zhangsan");
            assertEquals("zhangsan", redisTemplate.opsForValue().get(key));
        } finally {
            redisTemplate.delete(key);
        }
    }
}
