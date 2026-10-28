package com.publicservice.agent_system.complaint;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** 민원 접수 요청 */
public record ComplaintCreateRequest(

        @NotNull(message = "신청인 번호는 필수입니다.")
        Long applicantId,

        @NotNull(message = "업무 유형 번호는 필수입니다.")
        Long serviceTypeId,

        @NotBlank(message = "제목은 비어 있을 수 없습니다.")
        @Size(max = 200, message = "제목은 200자 이하여야 합니다.")
        String title,

        @NotBlank(message = "내용은 비어 있을 수 없습니다.")
        @Size(max = 2000, message = "내용은 2000자 이하여야 합니다.")
        String content
) {
}
