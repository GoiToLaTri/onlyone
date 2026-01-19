package com.zwind.identityservice.modules.permissions.repositiory;

import com.zwind.identityservice.modules.permissions.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {
    Optional<Permission> findByName(String s);

    boolean existsByName(String s);

    List<Permission> findAllByNameIn(Iterable<String> strings);
}
