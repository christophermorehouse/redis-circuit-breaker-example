package com.example.config;

import java.time.Duration;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SslOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.clusterA.host}")
    private String clusterAHost;

    @Value("${spring.redis.clusterA.port}")
    private int clusterAPort;

    @Value("${spring.redis.clusterB.host}")
    private String clusterBHost;

    @Value("${spring.redis.clusterB.port}")
    private int clusterBPort;

    @Value("${spring.redis.ssl.enabled:true}")
    private boolean sslEnabled;

    @Bean
    @Primary
    public LettuceConnectionFactory redisConnectionFactoryClusterA() {
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(2))
                .shutdownTimeout(Duration.ofMillis(100))
                .clientOptions(ClientOptions.builder()
                        .autoReconnect(true)  // Enable auto reconnect
                        .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                        .build())
                .useSsl()
                .disablePeerVerification()
                .build();

        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration(clusterAHost, clusterAPort);
        return new LettuceConnectionFactory(serverConfig, clientConfig);
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactoryClusterB() {
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(2))  // much shorter than the default 60s
                .shutdownTimeout(Duration.ofMillis(100))
                .clientOptions(ClientOptions.builder().autoReconnect(false).build())
                .useSsl()
                .disablePeerVerification()
                .build();

        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration(clusterBHost, clusterBPort);
        return new LettuceConnectionFactory(serverConfig, clientConfig);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplateClusterA() {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactoryClusterA());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplateClusterB() {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactoryClusterB());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}
