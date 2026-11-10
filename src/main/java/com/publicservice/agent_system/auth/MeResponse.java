package com.publicservice.agent_system.auth;

import com.publicservice.agent_system.employee.EmployeeRole;

/** 현재 로그인한 사용자 (비밀번호는 절대 포함하지 않음) */
public record MeResponse(
        Long employeeId,
        String loginId,
        String name,
        EmployeeRole role,
        String roleLabel,
        String departmentName
) {
    public static MeResponse from(LoginEmployee e) {
        return new MeResponse(e.getEmployeeId(), e.getLoginId(), e.getName(),
                e.getRole(), e.getRole().getLabel(), e.getDepartmentName());
    }
}
