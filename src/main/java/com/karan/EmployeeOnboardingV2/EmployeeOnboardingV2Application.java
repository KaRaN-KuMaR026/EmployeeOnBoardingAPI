package com.karan.EmployeeOnboardingV2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity
public class EmployeeOnboardingV2Application {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeOnboardingV2Application.class, args);
	}

}
