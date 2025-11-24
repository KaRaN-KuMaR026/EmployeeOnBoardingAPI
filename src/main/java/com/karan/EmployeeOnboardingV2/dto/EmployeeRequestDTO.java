package com.karan.EmployeeOnboardingV2.dto;

import com.karan.EmployeeOnboardingV2.entity.type.RoleType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class EmployeeRequestDTO {

    @NotNull(message = "User Id cannot be blank")
    private Long userId;

    private Set<RoleType> roles = new HashSet<>();

    @NotBlank(message = "PhoneNumber cannot be blank")
    @Pattern(regexp = "^[7-9][0-9]{9}$")
    private String phoneNumber;

    @NotNull(message = "Address cannot be blank")
    private AddressDTO address;

    @NotNull(message = "Department Id cannot be null")
    private Long deptId;

    @NotNull(message = "Designation cannot be null")
    private DesignationDTO designation;

}
