package com.example.controller;

import com.example.service.RedisService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/redis")
public class RedisController {

    private final RedisService redisService;

    public RedisController(RedisService redisService) {
        this.redisService = redisService;
    }

    @PostMapping("/set")
    public String setKey(@RequestParam String key, @RequestParam String value) {
        redisService.setKey(key, value);
        return "Key '" + key + "' set to '" + value + "' (with circuit breaker handling).";
    }

    @GetMapping("/get")
    public String getKey(@RequestParam String key) {
        String value = redisService.getKey(key);
        return "Value for key '" + key + "' is: " + value;
    }
}
