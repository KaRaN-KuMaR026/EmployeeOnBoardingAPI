package com.karan.EmployeeOnboardingV2.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PermissionType {
    EMPLOYEE_READ("employee:read"),
    EMPLOYEE_WRITE("employee:write"),
    EMPLOYEE_DELETE("employee:delete"),
    DEPARTMENT_READ("department:read"),
    DEPARTMENT_WRITE("department:write"),
    DEPARTMENT_DELETE("department:delete"),
    USER_MANAGE("user:manage"),
    PUBLIC_VIEW("public:view");

    private final String permission;
}
