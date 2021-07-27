package com.gateway.app.gatewayapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class SessionsResponseDto {
    private Set<String> result;
}
