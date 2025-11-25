package com.karan.EmployeeOnboardingV2.service;

import com.karan.EmployeeOnboardingV2.dto.*;
import com.karan.EmployeeOnboardingV2.entity.*;
import com.karan.EmployeeOnboardingV2.entity.type.RoleType;
import com.karan.EmployeeOnboardingV2.repository.DepartmentRepository;
import com.karan.EmployeeOnboardingV2.repository.DesignationRepository;
import com.karan.EmployeeOnboardingV2.repository.EmployeeRepository;
import com.karan.EmployeeOnboardingV2.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.Join;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
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
    public PagedResponseDTO getEmployees(
            int page,
            int size,
            String sortBy,
            String sortDir,
            Long id,
            String name,
            LocalDate dateOfJoining,
            String phoneNumber
    ) {
        log.info("Accessing Employee Details");

        if (page <= 0) {
            log.error("Page number must be greater than 0");
            throw new IllegalArgumentException("Page number must be greater than 0");
        }
        if (size <= 0) {
            log.error("Page size must be greater than 0");
            throw new IllegalArgumentException("Page size must be greater than 0");
        }

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Specification<Employee> spec = Specification.allOf();

        if(id != null) {
            log.info("Filtering employees using id");
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("id"), id)
            );
        }
        if(name != null && !name.isBlank()) {
            log.info("Filtering employees using name");
            spec = spec.and((root, query, cb) -> {
                Join<Employee, User> userJoin = root.join("user");
                return cb.like(
                        cb.lower(userJoin.get("name")), "%" + name.toLowerCase() + "%"
                );
            });
        }
        if(dateOfJoining != null) {
            log.info("Filtering employees using date of joining");
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("dateOfJoining"), dateOfJoining)
                    );
        }
        if(phoneNumber != null && !phoneNumber.isBlank()) {
            log.info("Filtering employees using phone number");
            spec = spec.and((root, query, cb) -> {
                Join<Employee, User> userJoin = root.join("user");
                return cb.equal(userJoin.get("phoneNumber"), phoneNumber);
            });
        }

        Page<EmployeeResponseDTO> employeePage = employeeRepository.findAll(spec, pageable).map(this::convertToResponseDTO);

        if (page > employeePage.getTotalPages() && employeePage.getTotalPages() > 0) {
            log.error("Requested page number exceeds total available pages");
            throw new IllegalArgumentException("Requested page number exceeds total available pages");
        }

        if (employeePage.getContent().isEmpty()) {
            log.error("No Employees found for the given filters");
            throw new NoResultException("No Employees found for the given filters");
        }

        return PagedResponseDTO
                .builder()
                .data(employeePage.getContent())
                .currentPage(employeePage.getNumber() + 1)
                .pageSize(employeePage.getSize())
                .totalElements(employeePage.getTotalElements())
                .totalPages(employeePage.getTotalPages())
                .last(employeePage.isLast())
                .build();
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
