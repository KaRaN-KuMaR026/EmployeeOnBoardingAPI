package com.karan.EmployeeOnboardingV2.service;

import com.karan.EmployeeOnboardingV2.dto.PublicEmployeeResponseDTO;
import com.karan.EmployeeOnboardingV2.entity.*;
import com.karan.EmployeeOnboardingV2.dto.EmployeeRequestDTO;
import com.karan.EmployeeOnboardingV2.dto.EmployeeResponseDTO;
import com.karan.EmployeeOnboardingV2.dto.EmployeeUpdateDTO;
import com.karan.EmployeeOnboardingV2.entity.type.RoleType;
import com.karan.EmployeeOnboardingV2.repository.DepartmentRepository;
import com.karan.EmployeeOnboardingV2.repository.DesignationRepository;
import com.karan.EmployeeOnboardingV2.repository.EmployeeRepository;
import com.karan.EmployeeOnboardingV2.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final UserRepository userRepository;

    //CREATE
    @Transactional
    @PreAuthorize("hasAuthority('employee:write')")
    public EmployeeResponseDTO onBoardEmployee(EmployeeRequestDTO requestDTO) {
        log.info("Employee onboarding initiated for User with Id {}", requestDTO.getUserId());
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> {
                    log.error("User not found with Id {}", requestDTO.getUserId());
                    return new EntityNotFoundException("User not found with Id " + requestDTO.getUserId());
                });

        if(requestDTO.getRoles() != null) {
            for(RoleType role : requestDTO.getRoles()) {
                user.getRoles().add(role);
                log.info("Adding ROLE : {} to User", role);
            }
        }

        user.getRoles().add(RoleType.EMPLOYEE);
        log.info("Adding ROLE : EMPLOYEE to User");
        user = userRepository.save(user);

        log.info("Assigning Department with Id {} to User", requestDTO.getDeptId());
        Department department = departmentRepository.findById(requestDTO.getDeptId())
                .orElseThrow(() -> {
                    log.error("Department not found with Id {}", requestDTO.getDeptId());
                    return new EntityNotFoundException("Department not found with Id " + requestDTO.getDeptId());
                });

        Designation designation;
        log.info("Assigning Designation {} to User", requestDTO.getDesignation().toString());
        if(requestDTO.getDesignation().getId() != null) {
            designation = designationRepository.findById(requestDTO.getDesignation().getId())
                    .orElseThrow(() -> {
                        log.error("Designation not found with Id {}", requestDTO.getDesignation().getId());
                        return new EntityNotFoundException("Designation not found with Id " + requestDTO.getDesignation().getId());
                    });
        }
        else {
            designation = Designation
                    .builder()
                    .name(requestDTO.getDesignation().getName())
                    .level(requestDTO.getDesignation().getLevel())
                    .build();
        }

        Address address = Address
                .builder()
                .street(requestDTO.getAddress().getStreet())
                .city(requestDTO.getAddress().getCity())
                .state(requestDTO.getAddress().getState())
                .zipCode(requestDTO.getAddress().getZipCode())
                .build();

        Employee employee = Employee
                .builder()
                .user(user)
                .phoneNumber(requestDTO.getPhoneNumber())
                .dateOfJoining(LocalDate.now())
                .address(address)
                .department(department)
                .designation(designation)
                .build();

        log.info("User Onboarded as Employee");
        return convertToResponseDTO(employeeRepository.save(employee));
    }

    //READ
    @Transactional
    @PreAuthorize("hasAuthority('public:view')")
    public List<PublicEmployeeResponseDTO> getEmployeesPublic() {
        log.info("Accessing Public Employees Details");
        Stream<Employee> stream = employeeRepository.findAll().stream()
                .sorted(Comparator.comparing(emp -> emp.getUser().getId()))
                .skip(1);
        return stream.map(this::convertToPublicResponseDTO).toList();
    }

    @Transactional
    @PreAuthorize("hasAuthority('employee:read')")
    public List<EmployeeResponseDTO> getEmployees(Long id, String name, LocalDate dateOfJoining, String phoneNumber, String sort) {
        log.info("Accessing Full Employee Details");
        Stream<Employee> stream = employeeRepository.findAll().stream()
                .filter(emp -> id == null || Objects.equals(emp.getUser().getId(), id))
                .filter(emp -> name == null || emp.getUser().getName().equalsIgnoreCase(name))
                .filter(emp -> dateOfJoining == null || emp.getDateOfJoining() == dateOfJoining)
                .filter(emp -> phoneNumber == null || Objects.equals(emp.getPhoneNumber(), phoneNumber))
                .skip(1);

        stream = switch(sort.toLowerCase()) {
            case "name" -> stream.sorted(Comparator.comparing(emp -> emp.getUser().getName()));
            case "dateofjoining" -> stream.sorted(Comparator.comparing(Employee::getDateOfJoining));
            default -> stream.sorted(Comparator.comparing(emp -> emp.getUser().getId()));
        };

        return stream.map(this::convertToResponseDTO).toList();
    }

    @Transactional
    @PreAuthorize("hasAuthority('employee:read')")
    public List<EmployeeResponseDTO> getEmployeesByDept(Long id) {
        log.info("Accessing Employees in Department with Id {}", id);
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Department not found with Id {}" , id);
                    return new EntityNotFoundException("Department not found with Id " + id);
                });
        return department.getEmployees().stream().map(this::convertToResponseDTO).toList();
    }

    @Transactional
    @PreAuthorize("#id == principal.id or hasAuthority('employee:read')")
    public EmployeeResponseDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with Id " + id));

        return convertToResponseDTO(employee);
    }

    //UPDATE
    @Transactional
    @PreAuthorize("#id == principal.id or hasAuthority('employee:write')")
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeUpdateDTO updateDTO) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with Id " + id));

        if(updateDTO.getName() != null) {
            employee.getUser().setName(updateDTO.getName());
        }
        if(updateDTO.getEmail() != null) {
            employee.getUser().setEmail(updateDTO.getEmail());
        }
        if(updateDTO.getPhoneNumber() != null) {
            employee.setPhoneNumber(updateDTO.getPhoneNumber());
        }
        if(updateDTO.getAddress() != null) {
            employee.setAddress(updateDTO.getAddress());
        }
        if(updateDTO.getDeptId() != null) {
            Department department = departmentRepository.findById(updateDTO.getDeptId())
                    .orElseThrow(() -> new EntityNotFoundException("Department not found with Id " + id));
            employee.setDepartment(department);
        }
        if(updateDTO.getDesignation() != null) {
            Designation designation;
            if(updateDTO.getDesignation().getId() != null) {
               designation = designationRepository.findById(updateDTO.getDesignation().getId())
                       .orElseThrow(() -> new EntityNotFoundException("Designation not found with Id " + id));
            }
            else {
                designation = Designation
                        .builder()
                        .name(updateDTO.getDesignation().getName())
                        .level(updateDTO.getDesignation().getLevel())
                        .build();
            }
            employee.setDesignation(designation);
        }
        return convertToResponseDTO(employeeRepository.save(employee));
    }

    //DELETE
    @Transactional
    @PreAuthorize("hasAuthority('employee:delete')")
    public void deleteEmployee(Long id) {
        if(!employeeRepository.existsById(id)) {
            throw new IllegalArgumentException("Employee with Id " + id + " does not exist");
        }
        employeeRepository.deleteById(id);
    }

    //Converter method for Employee Object to Public Response DTO
    public PublicEmployeeResponseDTO convertToPublicResponseDTO(Employee employee) {
        return PublicEmployeeResponseDTO
                .builder()
                .name(employee.getUser().getName())
                .deptName(employee.getDepartment() != null
                        ? employee.getDepartment().getName()
                        : "N/A")
                .designation(employee.getDesignation() != null
                        ? employee.getDesignation().toString()
                        : "N/A")
                .build();
    }

    //Converter method for Employee Object to Response DTO
    public EmployeeResponseDTO convertToResponseDTO(Employee employee) {

        return EmployeeResponseDTO
                .builder()
                .id(employee.getUser().getId())
                .name(employee.getUser().getName())
                .email(employee.getUser().getEmail())
                .phoneNumber(employee.getPhoneNumber())
                .address(employee.getAddress() != null
                        ? employee.getAddress().toString()
                        : "N/A")
                .deptName(employee.getDepartment() != null
                        ? employee.getDepartment().getName()
                        : "N/A")
                .designation(employee.getDesignation()!= null
                        ? employee.getDesignation().toString()
                        : "N/A")
                .build();
    }
}
