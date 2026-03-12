package com.example.portal.poratlmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CellDTO {
    private Long id;
    private String cellCode;
    private String cellName;
    private Long sectorId;
    private String sectorName;
    private String sectorCode;
}
