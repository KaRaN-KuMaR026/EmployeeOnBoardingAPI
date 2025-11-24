package com.karan.EmployeeOnboardingV2.controller;

import com.karan.EmployeeOnboardingV2.dto.DepartmentResponseDTO;
import com.karan.EmployeeOnboardingV2.dto.PublicEmployeeResponseDTO;
import com.karan.EmployeeOnboardingV2.service.DepartmentService;
import com.karan.EmployeeOnboardingV2.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    @GetMapping("/employees")
    public ResponseEntity<List<PublicEmployeeResponseDTO>> getEmployees() {
        return ResponseEntity.ok(employeeService.getEmployeesPublic());
    }

    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentResponseDTO>> getDepartments(
            @RequestParam(required = false)Long id,
            @RequestParam(required = false)String name,
            @RequestParam(required = false, defaultValue = "id")String sort
    ) {
        return ResponseEntity.ok(departmentService.getDepartments(id,name,sort));
    }
}
