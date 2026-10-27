package com.publicservice.agent_system.global.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

    private final String code;   // 프로그램이 읽는 이름표 (예: DEPARTMENT_NOT_FOUND)

    public NotFoundException(String code, String message) {
        super(message);          // 사람이 읽는 메시지는 부모(RuntimeException)에게 맡김
        this.code = code;
    }
}