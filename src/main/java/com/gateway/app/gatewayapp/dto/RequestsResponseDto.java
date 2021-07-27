package com.gateway.app.gatewayapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
public class RequestsResponseDto {
    private Set<UUID> result;
}
