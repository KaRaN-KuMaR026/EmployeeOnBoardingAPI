package com.karan.EmployeeOnboardingV2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDTO implements Serializable {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private String deptName;
    private String designation;
}
