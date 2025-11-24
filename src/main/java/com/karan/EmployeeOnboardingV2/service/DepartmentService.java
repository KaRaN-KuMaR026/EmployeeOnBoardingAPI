package com.karan.EmployeeOnboardingV2.service;

import com.karan.EmployeeOnboardingV2.entity.Department;
import com.karan.EmployeeOnboardingV2.entity.Employee;
import com.karan.EmployeeOnboardingV2.dto.DepartmentRequestDTO;
import com.karan.EmployeeOnboardingV2.dto.DepartmentResponseDTO;
import com.karan.EmployeeOnboardingV2.repository.DepartmentRepository;
import com.karan.EmployeeOnboardingV2.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    //CREATE
    @Transactional
    @PreAuthorize("hasAuthority('department:write')")
    public DepartmentResponseDTO addDepartment(DepartmentRequestDTO requestDTO) {
        Department department = Department.builder()
                .name(requestDTO.getName())
                .build();
        return convertToResponseDTO(departmentRepository.save(department));
    }

    //READ
    @Transactional
    @PreAuthorize("hasAnyAuthority('department:read', 'public:view')")
    public List<DepartmentResponseDTO> getDepartments(Long id, String name, String sort) {
        Stream<Department> stream = departmentRepository.findAll().stream()
                .filter(dept -> id == null || Objects.equals(dept.getId(), id))
                .filter(dept -> name == null || Objects.equals(dept.getName(), name));
        stream = switch(sort.toLowerCase()) {
            case "id" -> stream.sorted(Comparator.comparingLong(Department::getId));
            case "name" -> stream.sorted(Comparator.comparing(Department::getName));
            default -> throw new IllegalArgumentException("Invalid Sorting choice");
        };

        return stream.map(this::convertToResponseDTO).toList();
    }

    //UPDATE
    @Transactional
    @PreAuthorize("hasAuthority('department:write')")
    public DepartmentResponseDTO updateDepartment(DepartmentRequestDTO requestDTO, Long id) {
        Department department = departmentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Department not found with Id " + id));
        department.setName(requestDTO.getName());
        return convertToResponseDTO(departmentRepository.save(department));
    }

    //DELETE
    @Transactional
    @PreAuthorize("hasAuthority('department:delete')")
    public String deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Department with Id" + id + " does not exist"));
        List<Employee> list = employeeRepository.findByDepartmentId(id);
        for(Employee emp : list) {
            emp.setDepartment(null);
            emp.setDesignation(null);
        }
        departmentRepository.deleteById(id);
        return "Department deleted with Id " + id;
    }

    //Converter Method for Department Object to Response DTO
    private DepartmentResponseDTO convertToResponseDTO(Department department) {
        if(department.getEmployees() == null) {
            return new DepartmentResponseDTO(department.getId(), department.getName(), 0);
        }
        return new DepartmentResponseDTO(department.getId(), department.getName(), department.getEmployees().size());
    }
}
