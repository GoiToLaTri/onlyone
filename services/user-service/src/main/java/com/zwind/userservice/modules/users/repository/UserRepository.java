package com.zwind.userservice.modules.users.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zwind.userservice.modules.users.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByPublicId(String s);

    Optional<User> findByAccountId(String s);
}
