package com.gateway.app.gatewayapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gateway.app.gatewayapp.dto.RequestsResponseDto;
import com.gateway.app.gatewayapp.dto.SessionDto;
import com.gateway.app.gatewayapp.entity.Command;
import com.gateway.app.gatewayapp.service.SessionService;

@RestController
@RequestMapping("/xml_api")
@RequiredArgsConstructor
@Slf4j
public class SessionXmlController {

    private final SessionService sessionService;

    @PostMapping("/command")
    public ResponseEntity<RequestsResponseDto> postSession(@RequestBody SessionDto sessionDto) {
        log.info(sessionDto.toString());
        if (sessionDto.getCommand().equals(Command.CREATE)) {
            sessionService.saveSession(sessionDto, true);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(sessionService.saveAndGetAllRequestsForSession(sessionDto), HttpStatus.OK);
        }

    }
}
