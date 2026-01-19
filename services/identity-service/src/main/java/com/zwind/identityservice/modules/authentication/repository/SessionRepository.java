package com.zwind.identityservice.modules.authentication.repository;

import com.zwind.identityservice.enums.SessionStatus;
import com.zwind.identityservice.modules.authentication.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, String> {
    Optional<Session> findByToken(String s);

    Optional<Session> findByIdAndIsConsumed(Long id, boolean isConsumed);

    Optional<Session> findByTokenAndIsConsumed(String token, boolean isConsumed);

    List<Session> findAllByUserIdAndIsConsumed(String userId, boolean isConsumed);

    List<Session> findAllByUserIdAndStatus(String userId, SessionStatus status);

    List<Session> findAllByUserIdAndIsConsumedAndStatus(
            String userId,
            boolean isConsumed,
            SessionStatus status
    );

    @Modifying
    @Transactional
    @Query("UPDATE Session s SET s.isConsumed = true, s.status = :status WHERE s.userId = :userId AND s.isConsumed = false")
    void updateStatusByUserId(@Param("userId") String userId, @Param("status") SessionStatus status);
}
