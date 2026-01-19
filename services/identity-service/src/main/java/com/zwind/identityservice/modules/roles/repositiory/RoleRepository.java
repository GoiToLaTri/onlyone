package com.zwind.identityservice.modules.roles.repositiory;

import com.zwind.identityservice.modules.roles.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    boolean existsByName(String s);

    Optional<Role> findByName(String s);

    List<Role> findAllByNameIn(Iterable<String> strings);
}
