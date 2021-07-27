package com.gateway.app.gatewayapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.gateway.app.gatewayapp.dto.RequestsResponseDto;
import com.gateway.app.gatewayapp.dto.SessionDto;
import com.gateway.app.gatewayapp.dto.SessionsResponseDto;
import com.gateway.app.gatewayapp.entity.SessionEntity;
import com.gateway.app.gatewayapp.repository.SessionRepository;

import static com.gateway.app.gatewayapp.configuration.RedisConfiguration.QUEUES_PROCESSING_CACHE_NAME;
import static com.gateway.app.gatewayapp.configuration.RedisConfiguration.QUEUES_TOTAL_CACHE_NAME;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionService {

    private final SessionRepository sessionRepository;
    private final RabbitTemplate rabbitTemplate;
    private final RedisTemplate redisTemplate;
    private final BrokerRouter brokerRouter;

    public Collection<SessionDto> listSessions() {
        return sessionRepository.findAll().stream().map(SessionDto::new).collect(toList());
    }

    public void saveSession(SessionDto sessionDto, boolean isErrorOnDuplication) {
        if (StringUtils.isEmpty(sessionDto.getSessionId()) && sessionDto.getRequestId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing requestId.");
        }
        log.info("Incoming request {}", sessionDto);
        boolean requestIdExistsWithinSession = sessionRepository.findBySessionId(sessionDto.getSessionId()).stream().anyMatch(r -> r.equals(sessionDto.getRequestId()));
        if (requestIdExistsWithinSession) {

            if (isErrorOnDuplication) {
                log.error(String.format("Duplicate request id - %s", sessionDto));
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate entry.");
            }
        } else {
            String eligibleQueue = brokerRouter.pickQueue();
            log.info("Sending message to queue {}", eligibleQueue);
            rabbitTemplate.convertAndSend("test-exchange", eligibleQueue, sessionDto);
            redisTemplate.opsForZSet().incrementScore(QUEUES_TOTAL_CACHE_NAME, eligibleQueue, 1);
            redisTemplate.opsForValue().increment(eligibleQueue);
            //TODO does the request handling in terms of db and message broker needs transaction ?
            sessionRepository.save(new SessionEntity(sessionDto));
            log.info("TOTAL INVOCATIONS FOR QUEUE {} : {}", eligibleQueue, redisTemplate.opsForZSet().score(QUEUES_TOTAL_CACHE_NAME, eligibleQueue));
        }
    }

    public RequestsResponseDto saveAndGetAllRequestsForSession(SessionDto sessionDto) {
        saveSession(sessionDto, false);
        return new RequestsResponseDto(sessionRepository.findBySessionId(sessionDto.getSessionId()));
    }

    public SessionsResponseDto getUserSessions(String userId) {
       return new SessionsResponseDto(sessionRepository.findSessionByUserId(userId));
    }
}
