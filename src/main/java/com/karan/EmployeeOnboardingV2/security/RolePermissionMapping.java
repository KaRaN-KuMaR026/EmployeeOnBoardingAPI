package com.karan.EmployeeOnboardingV2.security;

import com.karan.EmployeeOnboardingV2.entity.type.PermissionType;
import com.karan.EmployeeOnboardingV2.entity.type.RoleType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.karan.EmployeeOnboardingV2.entity.type.PermissionType.*;
import static com.karan.EmployeeOnboardingV2.entity.type.RoleType.*;

public class RolePermissionMapping {
    private static final Map<RoleType, Set<PermissionType>> map = Map.of(
            ADMIN, Set.of(EMPLOYEE_READ, EMPLOYEE_WRITE, EMPLOYEE_DELETE, DEPARTMENT_READ, DEPARTMENT_WRITE, DEPARTMENT_DELETE, USER_MANAGE, PUBLIC_VIEW),
            HR, Set.of(EMPLOYEE_READ, EMPLOYEE_WRITE, EMPLOYEE_DELETE, DEPARTMENT_READ, PUBLIC_VIEW),
            EMPLOYEE, Set.of(PUBLIC_VIEW),
            PUBLIC, Set.of(PUBLIC_VIEW)
    );

    public static Set<SimpleGrantedAuthority> getAuthoritiesForRole(RoleType role) {
        return map.get(role).stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
    }
}
