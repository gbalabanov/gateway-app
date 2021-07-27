package com.gateway.app.gatewayapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gateway.app.gatewayapp.dto.SessionsResponseDto;
import com.gateway.app.gatewayapp.service.SessionService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UsersController {

    private final SessionService sessionService;

    @GetMapping("/sessions")
    public SessionsResponseDto listSessions(@RequestParam String userId) {

        return sessionService.getUserSessions(userId);
    }
}
