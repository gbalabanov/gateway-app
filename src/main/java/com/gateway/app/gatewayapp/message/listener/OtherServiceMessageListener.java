package com.gateway.app.gatewayapp.message.listener;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import com.gateway.app.gatewayapp.service.OtherService;

@Data
@RequiredArgsConstructor
@Slf4j
public class OtherServiceMessageListener implements MessageListener {

    @Override
    public void onMessage(final Message message) {
        log.info("Message ----- " + message);
    }
}
