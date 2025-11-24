package com.karan.EmployeeOnboardingV2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentRequestDTO {
    @NotBlank(message = "Name of department cannot be blank")
    @Pattern(regexp = "^[a-zA-Z -]+$", message = "Name can only contain alphabets, - , and spaces")
    private String name;
}
