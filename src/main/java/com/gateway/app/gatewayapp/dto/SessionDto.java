package com.gateway.app.gatewayapp.dto;

import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gateway.app.gatewayapp.entity.Command;
import com.gateway.app.gatewayapp.entity.SessionEntity;

@NoArgsConstructor
@XmlRootElement(name = "command")
@ToString
public class SessionDto {
    public UUID getRequestId() {
        return requestId;
    }

    @XmlAttribute(name = "id")
    public void setRequestId(final UUID requestId) {
        this.requestId = requestId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    public String getProducerId() {
        return producerId;
    }

    public void setProducerId(final String producerId) {
        this.producerId = producerId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }

    private UUID requestId;
    private long timestamp;
    private String producerId;
    private String sessionId;
    @JsonIgnore
    private Command command;

    public Command getCommand() {
        return command;
    }

    public void setCommand(final Command command) {
        this.command = command;
    }

    public SessionDto(SessionEntity sessionEntity) {
        this.requestId = sessionEntity.getRequestId();
        this.timestamp = sessionEntity.getTimestamp();
        this.producerId = sessionEntity.getProducerId();
        this.sessionId = sessionEntity.getSessionId();
    }

    static class EnterCommand extends GetSessionCommand {
        @XmlElement
        private Long timestamp;
        @XmlElement
        private String player;
    }

    static class GetSessionCommand {
        @XmlAttribute(name = "session")
        protected String sessionId;
    }

    @XmlElement(name = "enter")
    @JsonIgnore
    private void setEnterCommand(EnterCommand enterCommand) {
        this.timestamp = enterCommand.timestamp;
        this.sessionId = enterCommand.sessionId;
        this.producerId = enterCommand.player;
        this.setCommand(Command.CREATE);
    }

    @XmlElement(name = "get")
    @JsonIgnore
    private void setGetCommand(GetSessionCommand getCommand) {
        this.sessionId = getCommand.sessionId;
        this.setCommand(Command.GET);
    }
}
