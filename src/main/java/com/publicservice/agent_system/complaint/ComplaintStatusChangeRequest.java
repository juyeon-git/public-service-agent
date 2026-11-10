package com.publicservice.agent_system.complaint;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 민원 상태 변경 요청.
 * 변경하는 직원은 요청 본문으로 받지 않고 로그인 정보(세션)에서 꺼낸다 → 다른 사람인 척 할 수 없음
 * assigneeId: ASSIGNED(배정)일 때만 필요
 * memo: REJECTED(반려)일 때는 사유 필수
 */
public record ComplaintStatusChangeRequest(

        @NotNull(message = "변경할 상태는 필수입니다.")
        ComplaintStatus toStatus,

        Long assigneeId,

        @Size(max = 500, message = "메모는 500자 이하여야 합니다.")
        String memo
) {
}
