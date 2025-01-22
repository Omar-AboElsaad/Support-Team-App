package com.Support.SupportTeam.Repository;

import com.Support.SupportTeam.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesRepo extends JpaRepository<Role,Long> {
    Optional<Role> findByName(String role);
}
