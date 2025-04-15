package com.example.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplateClusterA;
    private final RedisTemplate<String, String> redisTemplateClusterB;

    public RedisService(@Qualifier("redisTemplateClusterA") RedisTemplate<String, String> redisTemplateClusterA,
                        @Qualifier("redisTemplateClusterB") RedisTemplate<String, String> redisTemplateClusterB) {
        this.redisTemplateClusterA = redisTemplateClusterA;
        this.redisTemplateClusterB = redisTemplateClusterB;
    }

    /**
     * Set a key-value in Cluster A by default, with fallback to Cluster B
     */
    @CircuitBreaker(name = "redisServiceCB", fallbackMethod = "setKeyFallback")
    public void setKey(String key, String value) {
        // This operation will be protected by the circuit breaker
        redisTemplateClusterA.opsForValue().set(key, value);
    }

    /**
     * Fallback method that gets invoked if setKey() fails (e.g., due to Redis downtime)
     */
    public void setKeyFallback(String key, String value, Throwable t) {
        // Log the error, then switch to Cluster B
        System.err.println("Primary cluster failed for setKey, using fallback. Error: " + t.getMessage());
        redisTemplateClusterB.opsForValue().set(key, value);
    }

    /**
     * Get a value from Cluster A by default, with fallback to Cluster B
     */
    @CircuitBreaker(name = "redisServiceCB", fallbackMethod = "getKeyFallback")
    public String getKey(String key) {
        return redisTemplateClusterA.opsForValue().get(key);
    }

    /**
     * Fallback method for getKey()
     */
    public String getKeyFallback(String key, Throwable t) {
        System.err.println("Primary cluster failed for getKey, using fallback. Error: " + t.getMessage());
        return redisTemplateClusterB.opsForValue().get(key);
    }
}
