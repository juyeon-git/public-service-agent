package com.publicservice.agent_system.global.exception;

import lombok.Getter;

/** 요청 형식은 맞지만 지금 상태에서는 할 수 없는 작업 (예: 완료된 민원을 다시 처리중으로) → 409 */
@Getter
public class ConflictException extends RuntimeException {

    private final String code;

    public ConflictException(String code, String message) {
        super(message);
        this.code = code;
    }
}
