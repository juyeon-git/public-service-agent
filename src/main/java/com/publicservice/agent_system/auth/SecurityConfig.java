package com.publicservice.agent_system.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ── 1. URL별 접근 규칙 (위에서부터 먼저 맞는 규칙 적용) ──
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()   // 로그인은 누구나
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/employees/**").hasRole("ADMIN")            // 직원 목록(배정용)은 관리자만
                        .anyRequest().authenticated())                                    // 나머지는 로그인 필요

                // ── 2. CSRF 방어: 쿠키(XSRF-TOKEN)로 토큰을 주고, 화면은 헤더(X-XSRF-TOKEN)로 돌려보낸다 ──
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())  // JS가 읽을 수 있어야 하므로
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)

                // ── 3. 로그아웃: POST /api/auth/logout → 세션 삭제, 204 응답 ──
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler((req, res, authentication) -> res.setStatus(HttpServletResponse.SC_NO_CONTENT)))

                // ── 4. 로그인 안 함 → 401, 권한 없음 → 403 (화면 이동 대신 JSON으로 응답) ──
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) ->
                                writeError(res, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED", "로그인이 필요합니다."))
                        .accessDeniedHandler((req, res, e) -> {
                            if (e instanceof CsrfException) {
                                writeError(res, HttpServletResponse.SC_FORBIDDEN, "CSRF_TOKEN_INVALID",
                                        "보안 토큰이 없거나 올바르지 않습니다. 새로고침 후 다시 시도하세요.");
                            } else {
                                writeError(res, HttpServletResponse.SC_FORBIDDEN, "ACCESS_DENIED", "이 기능을 사용할 권한이 없습니다.");
                            }
                        }))

                // API 서버이므로 로그인 전 요청 주소를 세션에 저장하는 기능은 쓰지 않음
                .requestCache(cache -> cache.disable());

        return http.build();
    }

    /** 비밀번호는 BCrypt 해시로 비교 (같은 비밀번호도 매번 다른 해시가 나옴: salt) */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** AuthController에서 아이디/비밀번호 확인에 사용 (UserDetailsService + PasswordEncoder 빈을 자동으로 사용) */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    private static void writeError(HttpServletResponse res, int status, String code, String message) throws IOException {
        res.setStatus(status);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write("{\"code\":\"" + code + "\",\"message\":\"" + message + "\"}");
    }

    /** CSRF 토큰은 꺼내 쓸 때 만들어지므로, 매 요청마다 꺼내서 XSRF-TOKEN 쿠키가 응답에 실리게 한다 */
    private static class CsrfCookieFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws ServletException, IOException {
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            if (csrfToken != null) {
                csrfToken.getToken();
            }
            chain.doFilter(request, response);
        }
    }
}
