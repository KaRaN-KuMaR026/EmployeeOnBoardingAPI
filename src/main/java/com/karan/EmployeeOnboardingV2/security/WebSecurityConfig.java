package com.karan.EmployeeOnboardingV2.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static com.karan.EmployeeOnboardingV2.entity.type.PermissionType.*;


@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        //AUTH-ENDPOINTS
                        .requestMatchers("/auth/login", "/auth/signup").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/auth/reset").hasAuthority(USER_MANAGE.getPermission())

                        //PUBLIC ENDPOINTS
                        .requestMatchers(HttpMethod.GET, "/public/**").hasAuthority(PUBLIC_VIEW.getPermission())

                        //DEPARTMENT ENDPOINTS
                        .requestMatchers(HttpMethod.POST, "/departments").hasAuthority(DEPARTMENT_WRITE.getPermission())
                        .requestMatchers(HttpMethod.PUT, "/departments/*").hasAuthority(DEPARTMENT_WRITE.getPermission())
                        .requestMatchers(HttpMethod.DELETE, "/departments/*").hasAuthority(DEPARTMENT_DELETE.getPermission())
                        .requestMatchers(HttpMethod.GET, "/departments/*/employees").hasAuthority(DEPARTMENT_READ.getPermission())

                        //SELF-SERVICE ENDPOINTS
                        .requestMatchers(HttpMethod.GET, "/employees/me").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/employees/me").authenticated()

                        //EMPLOYEES ENDPOINTS
                        .requestMatchers(HttpMethod.GET,"/employees").hasAuthority(EMPLOYEE_READ.getPermission())
                        .requestMatchers(HttpMethod.POST, "/employees").hasAuthority(EMPLOYEE_WRITE.getPermission())
                        .requestMatchers(HttpMethod.PUT, "/employees/*").hasAuthority(EMPLOYEE_WRITE.getPermission())
                        .requestMatchers(HttpMethod.DELETE, "/employees/*").hasAuthority(EMPLOYEE_DELETE.getPermission())

                        //Everything else
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionConfig -> exceptionConfig.accessDeniedHandler((request, response, accessDeniedException) -> {
                    handlerExceptionResolver.resolveException(request, response, null, accessDeniedException);
                }));
        return httpSecurity.build();
    }
}
