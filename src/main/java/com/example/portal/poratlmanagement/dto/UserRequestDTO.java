package com.example.portal.poratlmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

/**
 * DTO for creating/updating a User
 * 
 * IMPORTANT: When creating a User, you only need to specify the Location ID (Village type).
 * Through the Rwanda administrative hierarchy (Location with parent_id),
 * the User is automatically linked to the entire structure.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {
    private String username;
    private String email;
    private String password;
    
    /**
     * Location ID (must be a VILLAGE type) - This is the ONLY location field needed.
     * The User will automatically be linked to the full hierarchy through parent_id:
     * Village → Cell → Sector → District → Province
     */
    private UUID locationId;
    
    private Set<Long> roleIds;
}
