package com.publicservice.agent_system.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // 직원 목록에 부서 이름도 보여주므로 부서를 함께 조회 (N+1 방지)
    @Query("select e from Employee e join fetch e.department order by e.department.id, e.id")
    List<Employee> findAllWithDepartment();
}
