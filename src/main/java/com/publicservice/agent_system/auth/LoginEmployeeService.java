package com.publicservice.agent_system.auth;

import com.publicservice.agent_system.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Spring Security가 로그인할 때 "이 아이디의 사용자 정보"를 가져가는 곳 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginEmployeeService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        return employeeRepository.findByLoginIdWithDepartment(loginId)
                .map(LoginEmployee::new)
                .orElseThrow(() -> new UsernameNotFoundException("없는 아이디: " + loginId));
    }
}
