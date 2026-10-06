package com.publicservice.agent_system.department;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    //                                                 Entity 타입 ┘   └ PK 타입
}