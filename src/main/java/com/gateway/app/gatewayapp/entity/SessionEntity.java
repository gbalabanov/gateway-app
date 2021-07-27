package com.gateway.app.gatewayapp.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import com.gateway.app.gatewayapp.dto.SessionDto;

@Entity
@Data
@NoArgsConstructor
public class SessionEntity {

    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    private UUID requestId;
    private long timestamp;
    private String producerId;
    @NotNull
    private String sessionId;

    public SessionEntity(SessionDto sessionDto){
        this.producerId = sessionDto.getProducerId();
        this.timestamp = sessionDto.getTimestamp();
        this.requestId = sessionDto.getRequestId();
        this.sessionId = sessionDto.getSessionId();
    }
}
