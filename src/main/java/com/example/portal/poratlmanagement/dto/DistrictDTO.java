package com.example.portal.poratlmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistrictDTO {
    private Long id;
    private String districtCode;
    private String districtName;
    private Long provinceId;
    private String provinceName;
    private String provinceCode;
}
