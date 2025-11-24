package com.karan.EmployeeOnboardingV2.controller;

import com.karan.EmployeeOnboardingV2.dto.DepartmentRequestDTO;
import com.karan.EmployeeOnboardingV2.dto.DepartmentResponseDTO;
import com.karan.EmployeeOnboardingV2.dto.EmployeeResponseDTO;
import com.karan.EmployeeOnboardingV2.service.DepartmentService;
import com.karan.EmployeeOnboardingV2.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;
    private final EmployeeService employeeService;

    //DEPARTMENT END-POINTS
    @GetMapping("/{id}/employees")
    public ResponseEntity<List<EmployeeResponseDTO>> getEmployeesByDepartment(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeesByDept(id));
    }

    @PostMapping
    public ResponseEntity<DepartmentResponseDTO> addDepartment(@Valid @RequestBody DepartmentRequestDTO departmentRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.addDepartment(departmentRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponseDTO> updateDepartment(@Valid @RequestBody DepartmentRequestDTO departmentRequestDTO, @PathVariable Long id) {
        return ResponseEntity.ok(departmentService.updateDepartment(departmentRequestDTO, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDepartment(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.deleteDepartment(id));
    }
}
