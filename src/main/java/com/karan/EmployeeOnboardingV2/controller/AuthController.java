package com.karan.EmployeeOnboardingV2.controller;

import com.karan.EmployeeOnboardingV2.dto.*;
import com.karan.EmployeeOnboardingV2.security.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(authService.login(loginRequestDTO));
    }

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDTO> signup(@Valid @RequestBody SignupRequestDTO signupRequestDTO) {
        return ResponseEntity.ok(authService.signup(signupRequestDTO));
    }

    @PutMapping("/reset/password")
    public ResponseEntity<String> resetUserPassword(@RequestBody UserPasswordResetDTO passwordResetDTO) {
        return ResponseEntity.ok(authService.resetUserPassword(passwordResetDTO));
    }
}
