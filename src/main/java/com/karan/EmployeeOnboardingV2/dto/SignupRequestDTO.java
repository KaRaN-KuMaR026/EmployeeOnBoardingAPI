package com.karan.EmployeeOnboardingV2.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SignupRequestDTO {
    @NotNull(message = "Name cannot be blank")
    private String name;

    @Email
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotNull(message = "Blank Username")
    private String username;

    @NotNull(message = "Password cannot be left empty")
    private String password;
}
