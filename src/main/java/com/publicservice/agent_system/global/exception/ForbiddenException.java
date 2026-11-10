package com.publicservice.agent_system.global.exception;

import lombok.Getter;

/** 로그인은 했지만 이 작업을 할 권한이 없음 (예: 직원이 다른 사람 민원을 처리) → 403 */
@Getter
public class ForbiddenException extends RuntimeException {

    private final String code;

    public ForbiddenException(String code, String message) {
        super(message);
        this.code = code;
    }
}
