package com.Support.SupportTeam.Repository;

import com.Support.SupportTeam.Entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Long> {
    User findByEmail(String email);

    boolean existsByemail(@Email(message = "Invalid Email") @NotBlank String attr0);

    List<User> findByRoles_Name(String roleName);

    List<User> findByFirstName(String name);

    List<User> findByProjects_Name(String project);

    @Query("SELECT u FROM User u WHERE u.active = false ")
    List<User> findNotActiveUsers();

    void deleteUserByFirstName(String name);

    void deleteUserByEmail(String mail);
}
