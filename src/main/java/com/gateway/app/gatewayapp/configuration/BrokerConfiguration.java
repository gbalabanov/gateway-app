package com.gateway.app.gatewayapp.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import com.gateway.app.gatewayapp.service.OtherService;

import static com.gateway.app.gatewayapp.configuration.RedisConfiguration.QUEUES_PROCESSING_CACHE_NAME;
import static com.gateway.app.gatewayapp.configuration.RedisConfiguration.QUEUES_TOTAL_CACHE_NAME;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BrokerConfiguration {
    public static final String TOPIC_EXCHANGE_NAME = "sessions-exchange";
    public static final String QUEUE_NAME = "sessions-queue";
    public static final String ROUTING_KEY_PREFIX = "queue.";

    private final ApplicationContext applicationContext;
    private final ConnectionFactory connectionFactory;
    private final OtherService otherService;
    private final RedisTemplate redisTemplate;

    @Value("${gateway.broker.queues:1}")
    private int queuesNumber;

    /**
     * Used to bootstrap queues creation at runtime
     * TODO: find more elegant solution
     */
    @PostConstruct
    public void setQueues(){
        log.info("Found {} queues to be created.", queuesNumber);
        ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
        TopicExchange topicExchange = new TopicExchange("test-exchange");
        beanFactory.registerSingleton("test-exchange", topicExchange);
        while(queuesNumber > 0) {
            String queueName = "queue." + queuesNumber;
            String bindingName = "binding." + queuesNumber;
            Queue queue = new Queue(queueName);
            SimpleMessageListenerContainer container = generateSimpleMessageListenerContainer(queueName);
            beanFactory.registerSingleton(queueName, queue);
            beanFactory.registerSingleton(bindingName, BindingBuilder.bind(queue).to(topicExchange).with(ROUTING_KEY_PREFIX + queuesNumber));
            beanFactory.registerSingleton("listener" + queuesNumber, container);
            redisTemplate.opsForZSet().add(QUEUES_TOTAL_CACHE_NAME, ROUTING_KEY_PREFIX + queuesNumber, 1);
            redisTemplate.opsForValue().set(QUEUES_PROCESSING_CACHE_NAME, ROUTING_KEY_PREFIX + queuesNumber, 0);
            queuesNumber--;
        }
    }

    private SimpleMessageListenerContainer generateSimpleMessageListenerContainer(final String queueName) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(otherService);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter() {
        return new MessageListenerAdapter(otherService, "sendReceive");
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        final var rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory listenerContainerFactory() {
        final var factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        return factory;
    }
}
