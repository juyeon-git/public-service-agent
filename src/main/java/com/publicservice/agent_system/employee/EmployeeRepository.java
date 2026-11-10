package com.publicservice.agent_system.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // 직원 목록에 부서 이름도 보여주므로 부서를 함께 조회 (N+1 방지)
    @Query("select e from Employee e join fetch e.department order by e.department.id, e.id")
    List<Employee> findAllWithDepartment();

    // 로그인할 때 아이디로 직원 조회 (로그인 정보에 부서도 담으므로 함께 조회)
    @Query("select e from Employee e join fetch e.department where e.loginId = :loginId")
    Optional<Employee> findByLoginIdWithDepartment(@Param("loginId") String loginId);
}
