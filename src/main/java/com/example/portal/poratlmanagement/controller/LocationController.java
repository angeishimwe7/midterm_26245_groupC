package com.example.portal.poratlmanagement.controller;

import com.example.portal.poratlmanagement.dto.LocationDTO;
import com.example.portal.poratlmanagement.dto.PagedResponseDTO;
import com.example.portal.poratlmanagement.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * LocationController - REST API for Location operations with Rwanda's administrative hierarchy.
 * 
 * LOGIC EXPLANATION:
 * - Provides endpoints for location CRUD operations
 * - Supports Rwanda's hierarchy: PROVINCE → DISTRICT → SECTOR → CELL → VILLAGE
 * - Uses single Location table with 'type' column to distinguish levels
 * - Self-referencing parent_id creates the hierarchy
 * - Supports sorting and pagination
 */
@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    /**
     * CREATE LOCATION endpoint
     * 
     * LOGIC EXPLANATION:
     * - Receives location data in request body
     * - Service validates parent exists (except for PROVINCE)
     * - Creates Location entity with parent reference
     * - Returns created location with hierarchy details
     */
    @PostMapping
    public ResponseEntity<LocationDTO> createLocation(@RequestBody LocationDTO dto) {
        LocationDTO created = locationService.createLocation(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> getLocationById(@PathVariable UUID id) {
        LocationDTO location = locationService.getLocationById(id);
        return ResponseEntity.ok(location);
    }

    /**
     * Get all locations with PAGINATION and SORTING
     */
    @GetMapping
    public ResponseEntity<PagedResponseDTO<LocationDTO>> getAllLocations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        PagedResponseDTO<LocationDTO> response = locationService.getAllLocations(page, size, sortBy, sortDir);
        return ResponseEntity.ok(response);
    }

    /**
     * Get locations by type (PROVINCE, DISTRICT, SECTOR, CELL, VILLAGE)
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<LocationDTO>> getLocationsByType(@PathVariable String type) {
        List<LocationDTO> locations = locationService.getLocationsByType(type);
        return ResponseEntity.ok(locations);
    }

    /**
     * Get locations by type with PAGINATION and SORTING
     */
    @GetMapping("/type/{type}/paged")
    public ResponseEntity<PagedResponseDTO<LocationDTO>> getLocationsByTypePaged(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        PagedResponseDTO<LocationDTO> response = locationService.getLocationsByType(type, page, size, sortBy, sortDir);
        return ResponseEntity.ok(response);
    }

    /**
     * Get children of a location (e.g., get all Districts in a Province)
     */
    @GetMapping("/{parentId}/children")
    public ResponseEntity<List<LocationDTO>> getChildren(@PathVariable UUID parentId) {
        List<LocationDTO> children = locationService.getChildren(parentId);
        return ResponseEntity.ok(children);
    }

    /**
     * Get location by code
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<LocationDTO> getLocationByCode(@PathVariable String code) {
        LocationDTO location = locationService.getLocationByCode(code);
        return ResponseEntity.ok(location);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationDTO> updateLocation(@PathVariable UUID id, @RequestBody LocationDTO dto) {
        LocationDTO updated = locationService.updateLocation(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable UUID id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * EXISTSBY endpoint: Check if location exists by code
     */
    @GetMapping("/exists")
    public ResponseEntity<Map<String, Boolean>> existsByCode(@RequestParam String code) {
        boolean exists = locationService.existsByCode(code);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    /**
     * Search locations by name
     */
    @GetMapping("/search")
    public ResponseEntity<List<LocationDTO>> searchByName(@RequestParam String name) {
        List<LocationDTO> locations = locationService.searchByName(name);
        return ResponseEntity.ok(locations);
    }
}
