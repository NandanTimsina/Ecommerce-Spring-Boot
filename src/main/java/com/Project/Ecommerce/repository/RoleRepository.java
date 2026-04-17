package com.Project.Ecommerce.repository;

import com.Project.Ecommerce.model.AppRole;
import com.Project.Ecommerce.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByRoleName(AppRole appRole);
}
