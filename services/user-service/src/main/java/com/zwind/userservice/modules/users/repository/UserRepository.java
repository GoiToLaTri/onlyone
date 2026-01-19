package com.zwind.userservice.modules.users.repository;

import com.zwind.userservice.modules.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByPublicId(String s);

    Optional<User> findByAccountId(String s);
}
