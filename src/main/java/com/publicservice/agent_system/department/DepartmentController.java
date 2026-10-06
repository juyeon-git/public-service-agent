package com.publicservice.agent_system.department;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController                       // 요청을 받아 JSON으로 응답하는 Controller
@RequestMapping("/api/departments")   // 이 Controller의 기본 URL
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping                       // GET /api/departments
    public List<DepartmentResponse> getDepartments() {
        return departmentService.findAll();
    }
    @GetMapping("/{id}")                                       // GET /api/departments/{id}
    public DepartmentResponse getDepartment(@PathVariable Long id) {
        return departmentService.findById(id);
    }
}