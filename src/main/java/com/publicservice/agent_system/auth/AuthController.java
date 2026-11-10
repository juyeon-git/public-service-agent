package com.publicservice.agent_system.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

/**
 * 로그인 / 현재 사용자 조회.
 * 로그아웃(POST /api/auth/logout)은 SecurityConfig에서 Spring Security가 처리한다.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @PostMapping("/login")
    public MeResponse login(@Valid @RequestBody LoginRequest request,
                            HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        // ① 아이디/비밀번호 확인 (틀리면 AuthenticationException → 401)
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.loginId(), request.password()));

        // ② 세션 고정 공격 방지: 로그인 전 세션이 있었다면 세션 ID를 새로 바꾼다
        if (httpRequest.getSession(false) != null) {
            httpRequest.changeSessionId();
        }

        // ③ 로그인 정보를 세션에 저장 → 다음 요청부터 쿠키(JSESSIONID)로 "누구인지" 알 수 있다
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, httpRequest, httpResponse);

        LoginEmployee loginEmployee = (LoginEmployee) authentication.getPrincipal();
        log.info("로그인 성공: loginId={}, role={}", loginEmployee.getLoginId(), loginEmployee.getRole());
        return MeResponse.from(loginEmployee);
    }

    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal LoginEmployee loginEmployee) {
        return MeResponse.from(loginEmployee);
    }
}
