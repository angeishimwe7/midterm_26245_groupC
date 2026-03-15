package com.example.portal.poratlmanagement.repository;

import com.example.portal.poratlmanagement.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserProfileRepository - Repository interface for UserProfile entity.
 * 
 * LOGIC EXPLANATION:
 * - Provides CRUD operations for UserProfile
 * - Demonstrates One-to-One relationship queries
 * - Shows existBy functionality
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    /**
     * Find user profile by user ID
     * - Used in One-to-One relationship lookup
     */
    Optional<UserProfile> findByUserId(Long userId);

    /**
     * Find user profile by username through User entity
     * - Demonstrates querying through relationship
     */
    @Query("SELECT up FROM UserProfile up JOIN up.user u WHERE u.username = :username")
    Optional<UserProfile> findByUsername(@Param("username") String username);

    /**
     * Check if profile exists for a user
     * EXISTSBY: Verifies if a user has a profile
     */
    boolean existsByUserId(Long userId);

    /**
     * Check if phone number exists
     * EXISTSBY: Used for phone number uniqueness validation
     */
    boolean existsByPhoneNumber(String phoneNumber);
}
