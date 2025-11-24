package com.karan.EmployeeOnboardingV2.security;

import com.karan.EmployeeOnboardingV2.dto.*;
import com.karan.EmployeeOnboardingV2.entity.User;
import com.karan.EmployeeOnboardingV2.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.karan.EmployeeOnboardingV2.entity.type.RoleType.PUBLIC;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignUpResponseDTO signup(SignupRequestDTO signupRequestDTO) {
        User user = userRepository.findByUsername(signupRequestDTO.getUsername())
                .orElse(null);

        if(user != null) {
            throw new IllegalArgumentException("User already exists");
        }

        user = userRepository.save(
                User.builder()
                        .username(signupRequestDTO.getUsername())
                        .name(signupRequestDTO.getName())
                        .password(passwordEncoder.encode(signupRequestDTO.getPassword()))
                        .email(signupRequestDTO.getEmail())
                        .roles(Set.of(PUBLIC))
                        .build()
                );

        return new SignUpResponseDTO(user.getId(), user.getUsername());
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword())
        );

        User user = (User) authentication.getPrincipal();
        String token = authUtil.generateAccessToken(user);
        return new LoginResponseDTO(user.getId(), token);
    }

    public String resetUserPassword(UserPasswordResetDTO passwordResetDTO) {
        User user = userRepository.findByEmail(passwordResetDTO.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found with the email"));

        user.setPassword(passwordEncoder.encode(passwordResetDTO.getNewPassword()));
        userRepository.save(user);
        return "Password Reset Successfully for the User with Id " + user.getId();
    }
}
