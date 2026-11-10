package com.publicservice.agent_system.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "아이디를 입력하세요.")
        String loginId,

        @NotBlank(message = "비밀번호를 입력하세요.")
        String password
) {
    @Override
    public String toString() {          // 실수로 로그에 찍혀도 비밀번호가 남지 않도록
        return "LoginRequest[loginId=" + loginId + ", password=****]";
    }
}
