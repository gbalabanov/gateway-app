package com.gateway.app.gatewayapp;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.ActiveProfiles;

import com.gateway.app.gatewayapp.dto.SessionDto;
import com.gateway.app.gatewayapp.service.SessionService;

import static com.gateway.app.gatewayapp.configuration.RedisConfiguration.QUEUES_TOTAL_CACHE_NAME;

@SpringBootTest(classes = {GatewayAppApplication.class})
@ActiveProfiles("test")
class GatewayAppApplicationTests {

	@Autowired
	SessionService sessionService;

	@Autowired
	RedisTemplate redisTemplate;

	@Value("${total.requests.to.create:1000}")
	private int totalRequests;

	@Value("${gateway.broker.queues:20}")
	private int totalQueues;

	@Test
	void contextLoads() {}

	@Test
	void testSimultaneousInvocations() throws InterruptedException {
		Runnable requestSenderRunnable = () -> sessionService.saveSession(generateSessionDto(), true);

		Thread[] threads = new Thread[totalRequests];
		for (int i = 0;i<totalRequests;i++){
			threads[0] = new Thread(requestSenderRunnable);
		}

		for (int i = 0;i<totalRequests;i++){
			threads[0].run();
		}

		for (int i = 0;i<totalRequests;i++){
			threads[0].join();
		}

		Assertions.assertEquals(totalQueues, redisTemplate.opsForZSet().rangeWithScores(QUEUES_TOTAL_CACHE_NAME, 0, Integer.MAX_VALUE).size());

		redisTemplate.opsForZSet().rangeWithScores(QUEUES_TOTAL_CACHE_NAME, 0, Integer.MAX_VALUE).forEach(o -> {
			ZSetOperations.TypedTuple<String> s = (ZSetOperations.TypedTuple)o;
			//score starts from 1
			Assertions.assertEquals(totalRequests/totalQueues + 1, s.getScore());
		});
	}

	private SessionDto generateSessionDto(){
		SessionDto sessionDto = new SessionDto();
		sessionDto.setProducerId("test-producer");
		sessionDto.setRequestId(UUID.randomUUID());
		sessionDto.setTimestamp(System.currentTimeMillis());
		sessionDto.setSessionId("session"+UUID.randomUUID().toString());
		return  sessionDto;
	}

}
