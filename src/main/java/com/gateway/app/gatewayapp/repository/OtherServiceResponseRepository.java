package com.gateway.app.gatewayapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gateway.app.gatewayapp.dto.OtherServiceResponseDto;
import com.gateway.app.gatewayapp.entity.SessionEntity;

@Repository
public interface OtherServiceResponseRepository extends JpaRepository<OtherServiceResponseDto, Long> {
}
