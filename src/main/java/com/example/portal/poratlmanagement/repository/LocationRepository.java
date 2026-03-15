package com.example.portal.poratlmanagement.repository;

import com.example.portal.poratlmanagement.model.Location;
import com.example.portal.poratlmanagement.model.Location.LocationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * LocationRepository - Repository interface for Location entity with hierarchy support.
 * 
 * LOGIC EXPLANATION:
 * - Provides CRUD operations for all location types (Province, District, Sector, Cell, Village)
 * - Uses LocationType enum to filter by administrative level
 * - Supports hierarchical queries (parent-child relationships)
 * - Demonstrates sorting and pagination
 */
@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {

    /**
     * Find locations by type (PROVINCE, DISTRICT, SECTOR, CELL, VILLAGE)
     */
    List<Location> findByType(LocationType type);

    /**
     * Find locations by type with pagination and sorting
     */
    Page<Location> findByType(LocationType type, Pageable pageable);

    /**
     * Find locations by parent ID (get children of a location)
     */
    List<Location> findByParentId(UUID parentId);

    /**
     * Find locations by parent ID with pagination
     */
    Page<Location> findByParentId(UUID parentId, Pageable pageable);

    /**
     * Find locations by type and parent ID
     * Example: Find all Districts in a Province
     */
    List<Location> findByTypeAndParentId(LocationType type, UUID parentId);

    /**
     * Find location by code (unique identifier)
     */
    Optional<Location> findByCode(String code);

    /**
     * Find location by code and type
     */
    Optional<Location> findByCodeAndType(String code, LocationType type);

    /**
     * Check if location exists by code
     */
    boolean existsByCode(String code);

    /**
     * Check if location exists by code and type
     */
    boolean existsByCodeAndType(String code, LocationType type);

    /**
     * Find all active locations by type
     */
    List<Location> findByTypeAndIsActiveTrue(LocationType type);

    /**
     * Search locations by name (case insensitive)
     */
    @Query("SELECT l FROM Location l WHERE LOWER(l.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Location> searchByName(@Param("search") String search);

    /**
     * Find all Villages in a Province (through the hierarchy)
     * Uses recursive query to find all descendants
     */
    @Query("SELECT l FROM Location l WHERE l.type = :type AND l.parent.id IN " +
           "(SELECT c.id FROM Location c WHERE c.parent.id IN " +
           "(SELECT s.id FROM Location s WHERE s.parent.id IN " +
           "(SELECT d.id FROM Location d WHERE d.parent.id = :provinceId)))"
    )
    List<Location> findByTypeAndProvinceId(@Param("type") LocationType type, @Param("provinceId") UUID provinceId);

    /**
     * Find all locations in a Province (any level below the province)
     */
    @Query("SELECT l FROM Location l WHERE l.parent.id = :parentId OR " +
           "l.parent.parent.id = :parentId OR " +
           "l.parent.parent.parent.id = :parentId OR " +
           "l.parent.parent.parent.parent.id = :parentId")
    List<Location> findAllDescendants(@Param("parentId") UUID parentId);
}
