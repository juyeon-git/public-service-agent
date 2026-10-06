package com.publicservice.agent_system.department;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service                          // 업무 로직을 담당하는 클래스로 Spring에 등록
@RequiredArgsConstructor          // Lombok: final 필드를 받는 생성자 자동 생성
@Transactional(readOnly = true)   // 이 클래스의 메서드는 기본적으로 "읽기 전용"
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public List<DepartmentResponse> findAll() {
        return departmentRepository.findAll()      // List<Department> (Entity)
                .stream()
                .map(DepartmentResponse::from)     // Entity → DTO로 변환
                .toList();
    }
    public DepartmentResponse findById(Long id) {
        Department department = departmentRepository.findById(id)  // ① Optional<Department>
                .orElseThrow();                                     // ② 꺼내기 (없으면 예외)
        return DepartmentResponse.from(department);                 // ③ Entity → DTO
    }
}