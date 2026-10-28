package com.publicservice.agent_system.employee;

public record EmployeeResponse(Long id, String name, Long departmentId, String departmentName) {

    public static EmployeeResponse from(Employee e) {
        return new EmployeeResponse(e.getId(), e.getName(), e.getDepartment().getId(), e.getDepartment().getName());
    }
}
