package com.karan.EmployeeOnboardingV2.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PublicEmployeeResponseDTO {
    private String name;
    private String deptName;
    private String designation;
}
