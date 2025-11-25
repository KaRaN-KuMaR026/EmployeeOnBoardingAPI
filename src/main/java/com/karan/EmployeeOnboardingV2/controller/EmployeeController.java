package com.karan.EmployeeOnboardingV2.controller;

import com.karan.EmployeeOnboardingV2.dto.*;
import com.karan.EmployeeOnboardingV2.entity.User;
import com.karan.EmployeeOnboardingV2.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final  EmployeeService employeeService;

    //SELF-SERVICE END-POINTS
    @GetMapping("/me")
    public ResponseEntity<EmployeeResponseDTO> getMyProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(employeeService.getEmployeeById(user.getId()));
    }

    @PutMapping("/me")
    public ResponseEntity<EmployeeResponseDTO> updateMyProfile(@AuthenticationPrincipal User user, @Valid @RequestBody EmployeeUpdateDTO updateDTO) {
        return ResponseEntity.ok(employeeService.updateEmployee(user.getId(), updateDTO));
    }

    //EMPLOYEE END-POINTS
    @GetMapping
    public ResponseEntity<PagedResponseDTO> getEmployees(
            @RequestParam(defaultValue = "1")int page,
            @RequestParam(defaultValue = "10")int size,
            @RequestParam(defaultValue = "id")String sortBy,
            @RequestParam(defaultValue = "asc")String sortDir,
            @RequestParam(required = false)Long id,
            @RequestParam(required = false)String name,
            @RequestParam(required = false) LocalDate dateOfJoining,
            @RequestParam(required = false)String phoneNumber
    ) {
        return ResponseEntity.ok(employeeService.getEmployees(page, size, sortBy, sortDir, id, name, dateOfJoining, phoneNumber));
    }

    @PostMapping
    public ResponseEntity<EmployeeResponseDTO> onBoardEmployee(@Valid @RequestBody EmployeeRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.onBoardEmployee(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(@Valid @RequestBody EmployeeUpdateDTO updateDTO, @PathVariable Long id) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, updateDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
