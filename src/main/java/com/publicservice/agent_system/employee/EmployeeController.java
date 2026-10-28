package com.publicservice.agent_system.employee;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping                       // GET /api/employees (담당자 선택 목록용)
    public List<EmployeeResponse> getEmployees() {
        return employeeService.findAll();
    }
}
