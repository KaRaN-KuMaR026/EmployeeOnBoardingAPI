package com.karan.EmployeeOnboardingV2.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class EmployeeResponseDTO implements Serializable {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private String deptName;
    private String designation;
}
