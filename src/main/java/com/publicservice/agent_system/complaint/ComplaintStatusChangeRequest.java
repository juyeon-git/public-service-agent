package com.publicservice.agent_system.complaint;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 민원 상태 변경 요청.
 * employeeId: 변경하는 직원 (로그인 기능을 만들면 로그인한 사용자로 대체 예정)
 * assigneeId: ASSIGNED(배정)일 때만 필요
 * memo: REJECTED(반려)일 때는 사유 필수
 */
public record ComplaintStatusChangeRequest(

        @NotNull(message = "변경할 상태는 필수입니다.")
        ComplaintStatus toStatus,

        @NotNull(message = "변경하는 직원 번호는 필수입니다.")
        Long employeeId,

        Long assigneeId,

        @Size(max = 500, message = "메모는 500자 이하여야 합니다.")
        String memo
) {
}
