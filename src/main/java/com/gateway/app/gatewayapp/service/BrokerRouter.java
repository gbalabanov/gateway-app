package com.gateway.app.gatewayapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import static com.gateway.app.gatewayapp.configuration.RedisConfiguration.QUEUES_TOTAL_CACHE_NAME;

@Service
@Slf4j
@RequiredArgsConstructor
public class BrokerRouter {

    private final RedisTemplate redisTemplate;

    /**
     * Pick the queue with the lowest number of messages in order to guarantee equal load on all queues
     * In other words, distributes requests to the queues in a round-robbin manner
     * @return
     */
    public String pickQueue(){
        return redisTemplate.opsForZSet().range(QUEUES_TOTAL_CACHE_NAME, 0, 0).iterator().next().toString();
    }
}
