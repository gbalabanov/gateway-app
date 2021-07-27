package com.gateway.app.gatewayapp.dto;

import lombok.Data;

import java.util.Map;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;
import org.hibernate.annotations.Type;

@Data
@Entity
public class OtherServiceResponseDto {
    @Id
    @GeneratedValue
    private Long id;
    @ElementCollection
    Map<String, String> args;
    @ElementCollection
    Map<String, String> headers;
}
