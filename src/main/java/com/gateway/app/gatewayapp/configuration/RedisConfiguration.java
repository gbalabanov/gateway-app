package com.gateway.app.gatewayapp.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfiguration {

    public static final String QUEUES_TOTAL_CACHE_NAME = "QUEUES";
    public static final String QUEUES_PROCESSING_CACHE_NAME = "QUEUES.PROCESSING";

    @Bean
    //this will serve as in-memory queues load tracker
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        //template.setValueSerializer(new GenericToStringSerializer<>(Long.class));
        return template;
    }
}
