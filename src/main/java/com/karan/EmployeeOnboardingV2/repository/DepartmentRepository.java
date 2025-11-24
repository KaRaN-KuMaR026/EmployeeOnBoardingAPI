package com.karan.EmployeeOnboardingV2.repository;

import com.karan.EmployeeOnboardingV2.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
