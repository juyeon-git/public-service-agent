package com.publicservice.agent_system.employee;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public List<EmployeeResponse> findAll() {
        return employeeRepository.findAllWithDepartment().stream()
                .map(EmployeeResponse::from)
                .toList();
    }
}
