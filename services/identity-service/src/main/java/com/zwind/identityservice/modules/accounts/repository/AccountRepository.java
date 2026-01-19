package com.zwind.identityservice.modules.accounts.repository;

import com.zwind.identityservice.modules.accounts.entity.Account;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    boolean existsByEmail(String s);

    @Override
    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    @NullMarked
    Optional<Account> findById(String s);

    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<Account> findByEmail(String s);

    @Query("SELECT a FROM Account a " +
            "LEFT JOIN FETCH a.roles r " +
            "LEFT JOIN FETCH r.permissions " +
            "WHERE a.id = :id")
    Optional<Account> findByIdWithRoles(@Param("id") String id);

    Optional<Account> findByEmailAndProvider(String email, String provider);
}
