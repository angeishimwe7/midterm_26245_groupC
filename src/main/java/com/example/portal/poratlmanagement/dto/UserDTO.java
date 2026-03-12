package com.example.portal.poratlmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private Boolean isActive;
    private LocalDateTime createdAt;
    
    // Location info (User is linked to a Village location)
    private UUID locationId;
    private String locationName;
    private String locationCode;
    private String locationType;
    
    // Full hierarchy path (e.g., "Kigali > Gasabo > Remera > Rukiri I > Umubano")
    private String locationPath;
    
    private Set<String> roles;
}
