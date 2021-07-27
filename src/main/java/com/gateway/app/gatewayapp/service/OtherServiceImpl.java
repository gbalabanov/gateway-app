package com.gateway.app.gatewayapp.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gateway.app.gatewayapp.configuration.BrokerConfiguration;
import com.gateway.app.gatewayapp.dto.OtherServiceResponseDto;
import com.gateway.app.gatewayapp.dto.SessionDto;
import com.gateway.app.gatewayapp.repository.OtherServiceResponseRepository;

import static com.gateway.app.gatewayapp.configuration.RedisConfiguration.QUEUES_PROCESSING_CACHE_NAME;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtherServiceImpl implements OtherService {

    public static String OTHER_SERVICE_URL = "https://postman-echo.com/get";

    @Value("${broker.request.timeout.millis:2000}")
    private int requestTimeout;

    private final RestTemplate restTemplate = new RestTemplate();
    private final OtherServiceResponseRepository otherServiceResponseRepository;
    private final ObjectMapper objectMapper;
    private final RedisTemplate redisTemplate;

    @Override
    public void sendReceive(SessionDto sessionDto) throws InterruptedException {
        URI endpoint = UriComponentsBuilder.fromHttpUrl(OTHER_SERVICE_URL)
                .queryParam("sessionId", sessionDto.getSessionId())
                .queryParam("requestId", sessionDto.getRequestId()).build().toUri();
        log.info("Sending {} to {}", sessionDto, this.getClass().getSimpleName());
        OtherServiceResponseDto otherServiceResponseDto = restTemplate.getForEntity(endpoint, OtherServiceResponseDto.class).getBody();
        Thread.sleep(requestTimeout);
        log.info("Got response from {} - {}", this.getClass().getSimpleName(), otherServiceResponseDto);
        otherServiceResponseRepository.save(otherServiceResponseDto);
    }

    @SneakyThrows
    @Override
    public void onMessage(final Message message) {
        log.info("Got message from queue {}", message.getMessageProperties().getConsumerQueue());
        this.sendReceive(objectMapper.readValue(message.getBody(), SessionDto.class));
        redisTemplate.opsForValue().decrement(message.getMessageProperties().getConsumerQueue());
    }
}
