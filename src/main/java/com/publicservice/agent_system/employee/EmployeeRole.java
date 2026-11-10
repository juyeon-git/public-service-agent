package com.publicservice.agent_system.employee;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmployeeRole {
    STAFF("직원"),     // 배정받은 민원만 처리
    ADMIN("관리자");   // 배정 + 모든 민원 처리

    private final String label;
}
