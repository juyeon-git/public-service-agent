package com.publicservice.agent_system.complaint;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ComplaintStatus {

    RECEIVED("접수"),
    ASSIGNED("배정"),
    IN_PROGRESS("처리중"),
    COMPLETED("완료"),
    REJECTED("반려");

    private final String label;   // 화면에 보여줄 한글 이름
}
