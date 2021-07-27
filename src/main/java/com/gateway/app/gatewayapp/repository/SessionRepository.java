package com.gateway.app.gatewayapp.repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gateway.app.gatewayapp.entity.SessionEntity;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity, Long> {

    @Query("SELECT requestId FROM SessionEntity s WHERE s.sessionId = ?1")
    Set<UUID> findBySessionId(String sessionId);

    @Query("SELECT sessionId FROM SessionEntity s WHERE s.producerId = ?1")
    Set<String> findSessionByUserId(String userId);
}
