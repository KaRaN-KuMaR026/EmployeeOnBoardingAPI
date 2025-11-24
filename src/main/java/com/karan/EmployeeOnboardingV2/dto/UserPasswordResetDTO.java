package com.karan.EmployeeOnboardingV2.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserPasswordResetDTO {

    @Email(message = "Invalid Email")
    String email;

    @Size(min = 4, max = 15, message = "Password should be 4-15 characters long")
    @NotBlank(message = "New Password cannot be blank")
    String newPassword;
}
