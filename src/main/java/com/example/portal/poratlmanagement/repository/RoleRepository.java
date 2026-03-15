package com.example.portal.poratlmanagement.repository;

import com.example.portal.poratlmanagement.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * RoleRepository - Repository interface for Role entity.
 * 
 * LOGIC EXPLANATION:
 * - Provides CRUD operations for Role
 * - Used in Many-to-Many relationship with User
 * - Demonstrates existBy functionality
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Find role by its name
     */
    Optional<Role> findByRoleName(String roleName);

    /**
     * Check if role exists by name
     * EXISTSBY: Used to prevent duplicate role creation
     */
    boolean existsByRoleName(String roleName);

    /**
     * Check if role is assigned to any user
     * EXISTSBY: Checks if role is in use before deletion
     */
    boolean existsByUsersIsNotEmpty();
}
