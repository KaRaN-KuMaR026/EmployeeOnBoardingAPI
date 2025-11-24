package com.karan.EmployeeOnboardingV2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentResponseDTO {
    private Long id;
    private String name;
    private Integer employeeCount;
}
