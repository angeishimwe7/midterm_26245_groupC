package com.example.portal.poratlmanagement.service;

import com.example.portal.poratlmanagement.dto.LocationDTO;
import com.example.portal.poratlmanagement.dto.PagedResponseDTO;
import com.example.portal.poratlmanagement.model.Location;
import com.example.portal.poratlmanagement.model.Location.LocationType;
import com.example.portal.poratlmanagement.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * LocationService - Business logic for Location operations with hierarchy support.
 * 
 * LOGIC EXPLANATION:
 * - Handles all administrative levels: PROVINCE, DISTRICT, SECTOR, CELL, VILLAGE
 * - Uses self-referencing relationship (parent_id) for hierarchy
 * - Provides methods to navigate the hierarchy
 */
@Service
@Transactional
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    /**
     * Create a new location with hierarchy
     */
    public LocationDTO createLocation(LocationDTO dto) {
        // Validate parent exists (except for PROVINCE which has no parent)
        Location parent = null;
        if (dto.getParentId() != null) {
            parent = locationRepository.findById(dto.getParentId())
                .orElseThrow(() -> new RuntimeException("Parent location not found"));
        }

        // Check if code already exists
        if (locationRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Location with code already exists: " + dto.getCode());
        }

        Location location = new Location();
        location.setCode(dto.getCode());
        location.setName(dto.getName());
        location.setType(LocationType.valueOf(dto.getType()));
        location.setDescription(dto.getDescription());
        location.setParent(parent);
        location.setIsActive(true);

        Location saved = locationRepository.save(location);
        return convertToDTO(saved);
    }

    /**
     * Get all locations with pagination
     */
    public PagedResponseDTO<LocationDTO> getAllLocations(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Location> locationPage = locationRepository.findAll(pageable);
        
        return convertToPagedResponse(locationPage);
    }

    /**
     * Get locations by type (PROVINCE, DISTRICT, SECTOR, CELL, VILLAGE)
     */
    public List<LocationDTO> getLocationsByType(String type) {
        return locationRepository.findByType(LocationType.valueOf(type)).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Get locations by type with pagination
     */
    public PagedResponseDTO<LocationDTO> getLocationsByType(String type, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Location> locationPage = locationRepository.findByType(LocationType.valueOf(type), pageable);
        
        return convertToPagedResponse(locationPage);
    }

    /**
     * Get location by ID
     */
    public LocationDTO getLocationById(UUID id) {
        Location location = locationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Location not found"));
        return convertToDTO(location);
    }

    /**
     * Get children of a location
     */
    public List<LocationDTO> getChildren(UUID parentId) {
        return locationRepository.findByParentId(parentId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Get location by code
     */
    public LocationDTO getLocationByCode(String code) {
        Location location = locationRepository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Location not found with code: " + code));
        return convertToDTO(location);
    }

    /**
     * Update location
     */
    public LocationDTO updateLocation(UUID id, LocationDTO dto) {
        Location location = locationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Location not found"));

        Location parent = null;
        if (dto.getParentId() != null) {
            parent = locationRepository.findById(dto.getParentId())
                .orElseThrow(() -> new RuntimeException("Parent location not found"));
        }

        location.setCode(dto.getCode());
        location.setName(dto.getName());
        location.setType(LocationType.valueOf(dto.getType()));
        location.setDescription(dto.getDescription());
        location.setParent(parent);

        Location updated = locationRepository.save(location);
        return convertToDTO(updated);
    }

    /**
     * Delete location
     */
    public void deleteLocation(UUID id) {
        if (!locationRepository.existsById(id)) {
            throw new RuntimeException("Location not found");
        }
        locationRepository.deleteById(id);
    }

    /**
     * Check if location exists by code
     */
    public boolean existsByCode(String code) {
        return locationRepository.existsByCode(code);
    }

    /**
     * Search locations by name
     */
    public List<LocationDTO> searchByName(String search) {
        return locationRepository.searchByName(search).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private PagedResponseDTO<LocationDTO> convertToPagedResponse(Page<Location> page) {
        List<LocationDTO> content = page.getContent().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return new PagedResponseDTO<>(
            content,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isLast()
        );
    }

    private LocationDTO convertToDTO(Location location) {
        LocationDTO dto = new LocationDTO();
        dto.setId(location.getId());
        dto.setCode(location.getCode());
        dto.setName(location.getName());
        dto.setType(location.getType().name());
        dto.setDescription(location.getDescription());
        dto.setIsActive(location.getIsActive());
        
        if (location.getParent() != null) {
            dto.setParentId(location.getParent().getId());
            dto.setParentName(location.getParent().getName());
            dto.setParentCode(location.getParent().getCode());
        }
        
        dto.setFullPath(buildFullPath(location));
        
        return dto;
    }

    /**
     * Build full hierarchy path (e.g., "Kigali > Gasabo > Remera > Rukiri I > Umubano")
     */
    private String buildFullPath(Location location) {
        StringBuilder path = new StringBuilder(location.getName());
        Location current = location.getParent();
        while (current != null) {
            path.insert(0, current.getName() + " > ");
            current = current.getParent();
        }
        return path.toString();
    }
}
