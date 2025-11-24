package com.karan.EmployeeOnboardingV2.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private String deptName;
    private String designation;
}
