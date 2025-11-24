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
import java.util.List;

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
    public ResponseEntity<List<EmployeeResponseDTO>> getEmployees(
            @RequestParam(required = false)Long id,
            @RequestParam(required = false)String name,
            @RequestParam(required = false, name = "dateofjoining") LocalDate dateOfJoining,
            @RequestParam(required = false, name = "phonenumber")String phoneNumber,
            @RequestParam(required = false, defaultValue = "id")String sort
    ) {
        return ResponseEntity.ok(employeeService.getEmployees(id, name, dateOfJoining, phoneNumber, sort));
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
