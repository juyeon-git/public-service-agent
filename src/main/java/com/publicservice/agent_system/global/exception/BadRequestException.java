package com.publicservice.agent_system.global.exception;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {

    private final String code;   // 예: INVALID_PERIOD

    public BadRequestException(String code, String message) {
        super(message);
        this.code = code;
    }
}
