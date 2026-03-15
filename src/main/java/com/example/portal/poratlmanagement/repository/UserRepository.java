package com.example.portal.poratlmanagement.repository;

import com.example.portal.poratlmanagement.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UserRepository - Repository interface for User entity.
 * 
 * LOGIC EXPLANATION:
 * - Provides CRUD operations and custom queries for User
 * - Includes methods to retrieve users by province (code or name)
 * - Demonstrates existBy functionality
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * RETRIEVE USERS BY PROVINCE CODE:
     * 
     * LOGIC EXPLANATION:
     * - Uses JPQL query to join User with Location hierarchy
     * - Filters users where location's ancestor has the given province code
     * - This demonstrates the Rwanda administrative hierarchy with single table
     * 
     * QUERY LOGIC:
     * - User is linked to a Location (Village type)
     * - We traverse up the hierarchy through parent_id
     * - WHERE clause filters by province code
     * - Returns List of users belonging to that province
     */
    @Query("SELECT u FROM User u JOIN FETCH u.location l WHERE l.parent.parent.parent.parent.code = :provinceCode AND l.parent.parent.parent.parent.type = 'PROVINCE'")
    List<User> findByProvinceCode(@Param("provinceCode") String provinceCode);

    /**
     * RETRIEVE USERS BY PROVINCE CODE with PAGINATION:
     */
    @Query("SELECT u FROM User u JOIN u.location l WHERE l.parent.parent.parent.parent.code = :provinceCode AND l.parent.parent.parent.parent.type = 'PROVINCE'")
    Page<User> findByProvinceCode(@Param("provinceCode") String provinceCode, Pageable pageable);

    /**
     * RETRIEVE USERS BY PROVINCE NAME:
     */
    @Query("SELECT u FROM User u JOIN FETCH u.location l WHERE l.parent.parent.parent.parent.name = :provinceName AND l.parent.parent.parent.parent.type = 'PROVINCE'")
    List<User> findByProvinceName(@Param("provinceName") String provinceName);

    /**
     * RETRIEVE USERS BY PROVINCE NAME with PAGINATION:
     */
    @Query("SELECT u FROM User u JOIN u.location l WHERE l.parent.parent.parent.parent.name = :provinceName AND l.parent.parent.parent.parent.type = 'PROVINCE'")
    Page<User> findByProvinceName(@Param("provinceName") String provinceName, Pageable pageable);

    /**
     * RETRIEVE USERS BY LOCATION CODE (any level):
     * - Can query by Province, District, Sector, Cell, or Village code
     */
    @Query("SELECT u FROM User u JOIN FETCH u.location l WHERE l.code = :locationCode")
    List<User> findByLocationCode(@Param("locationCode") String locationCode);

    /**
     * RETRIEVE USERS BY LOCATION NAME:
     */
    @Query("SELECT u FROM User u JOIN FETCH u.location l WHERE l.name = :locationName")
    List<User> findByLocationName(@Param("locationName") String locationName);

    /**
     * Find users by location ID
     */
    List<User> findByLocationId(Long locationId);

    /**
     * Find users by location ID with pagination
     */
    Page<User> findByLocationId(Long locationId, Pageable pageable);

    /**
     * EXISTSBY IMPLEMENTATION:
     * 
     * LOGIC EXPLANATION:
     * - existsByUsername: Checks if user with given username exists
     * - Spring Data JPA derives query from method name
     * - Generates efficient EXISTS SQL query
     * 
     * HOW EXISTSBY WORKS:
     * - Spring Data JPA parses method name pattern "existsBy{Field}"
     * - Generates: SELECT EXISTS(SELECT 1 FROM users WHERE username = ?)
     * - More efficient than count query as it stops at first match
     * - Returns boolean: true if exists, false otherwise
     * 
     * USE CASES:
     * - Username availability check during registration
     * - Email uniqueness validation
     * - Preventing duplicate entries
     */
    boolean existsByUsername(String username);

    /**
     * Check if user exists by email
     */
    boolean existsByEmail(String email);

    /**
     * Check if user exists by username or email
     */
    boolean existsByUsernameOrEmail(String username, String email);

    /**
     * Find active users with pagination
     */
    Page<User> findByIsActiveTrue(Pageable pageable);

    /**
     * Search users by username or email with pagination
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<User> searchUsers(@Param("search") String search, Pageable pageable);

    /**
     * Find users by role name with pagination
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleName = :roleName")
    Page<User> findByRoleName(@Param("roleName") String roleName, Pageable pageable);
}
