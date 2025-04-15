package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class SpringRedisResilience4jApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringRedisResilience4jApplication.class, args);
    }
}