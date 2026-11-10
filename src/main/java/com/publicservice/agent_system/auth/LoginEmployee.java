package com.publicservice.agent_system.auth;

import com.publicservice.agent_system.employee.Employee;
import com.publicservice.agent_system.employee.EmployeeRole;
import lombok.Getter;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * 로그인한 직원 정보. 로그인에 성공하면 세션에 저장되고,
 * Controller에서 @AuthenticationPrincipal 로 꺼내 쓴다.
 * (Entity를 세션에 그대로 넣지 않고 필요한 값만 복사해 둔다)
 */
@Getter
public class LoginEmployee implements UserDetails, CredentialsContainer {

    private final Long employeeId;
    private final String loginId;
    private final String name;
    private final EmployeeRole role;
    private final String departmentName;
    private String password;            // 비밀번호 해시. 로그인 확인이 끝나면 지운다

    public LoginEmployee(Employee e) {
        this.employeeId = e.getId();
        this.loginId = e.getLoginId();
        this.name = e.getName();
        this.role = e.getRole();
        this.departmentName = e.getDepartment().getName();
        this.password = e.getPasswordHash();
    }

    public boolean isAdmin() {
        return role == EmployeeRole.ADMIN;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));   // hasRole("ADMIN") 검사에 사용
    }

    @Override
    public String getUsername() {
        return loginId;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void eraseCredentials() {
        this.password = null;           // 세션에 비밀번호 해시가 남지 않도록
    }
}
