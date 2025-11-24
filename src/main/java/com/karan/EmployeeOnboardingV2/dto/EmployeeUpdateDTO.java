package com.karan.EmployeeOnboardingV2.dto;

import com.karan.EmployeeOnboardingV2.entity.Address;
import com.karan.EmployeeOnboardingV2.entity.Designation;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class EmployeeUpdateDTO {
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "Name can only contain alphabets and spaces")
    private String name;

    @Email(message = "Invalid Email")
    private String email;

    @Pattern(regexp = "^[7-9][0-9]{9}$")
    private String phoneNumber;

    private Address address;

    private Long deptId;

    private Designation designation;
}
