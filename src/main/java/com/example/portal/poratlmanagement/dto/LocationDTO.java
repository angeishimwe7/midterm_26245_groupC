package com.example.portal.poratlmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for Location entity representing Rwanda's administrative hierarchy.
 * 
 * LOGIC EXPLANATION:
 * - Single DTO handles all location types: PROVINCE, DISTRICT, SECTOR, CELL, VILLAGE
 * - Type field determines the administrative level
 * - ParentId creates the hierarchy relationship
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {
    private UUID id;
    private String code;
    private String name;
    private String type;  // PROVINCE, DISTRICT, SECTOR, CELL, VILLAGE
    private String description;
    private Boolean isActive;
    
    // Parent relationship
    private UUID parentId;
    private String parentName;
    private String parentCode;
    
    // Full path for display (e.g., "Kigali > Gasabo > Remera > Rukiri I > Umubano")
    private String fullPath;
}
