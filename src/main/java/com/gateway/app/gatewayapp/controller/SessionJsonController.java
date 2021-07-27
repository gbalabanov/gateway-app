package com.gateway.app.gatewayapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gateway.app.gatewayapp.dto.RequestsResponseDto;
import com.gateway.app.gatewayapp.dto.SessionDto;
import com.gateway.app.gatewayapp.service.OtherService;
import com.gateway.app.gatewayapp.service.SessionService;

@RestController
@RequestMapping("/json_api")
@RequiredArgsConstructor
@Slf4j
public class SessionJsonController {

    private final SessionService sessionService;
    private final OtherService otherService;


    @PostMapping("/insert")
    public void postSession(@RequestBody @Valid SessionDto sessionDto) {
        log.info(sessionDto.toString());
        sessionService.saveSession(sessionDto, true);
    }

    @GetMapping("/list")
    public Collection<SessionDto> listSessions() {
        return sessionService.listSessions();
    }

    @PostMapping("/find")
    public RequestsResponseDto saveAndGetRequestsBySessionId(@RequestBody @Valid SessionDto sessionDto) {
        return sessionService.saveAndGetAllRequestsForSession(sessionDto);
    }
}
