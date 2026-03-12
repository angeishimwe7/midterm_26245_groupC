package com.example.portal.poratlmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating/updating a Location
 * Used to receive location data from client
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationRequestDTO {
    private String address;
    private String city;
    private String postalCode;
    private Double latitude;
    private Double longitude;
    private Long provinceId;
}
