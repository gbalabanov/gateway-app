package com.gateway.app.gatewayapp.service;

import java.io.IOException;

import org.springframework.amqp.core.MessageListener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gateway.app.gatewayapp.dto.SessionDto;

public interface OtherService extends MessageListener {

    void sendReceive(SessionDto sessionDto) throws InterruptedException, IOException;
}
