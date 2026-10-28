package com.publicservice.agent_system.complaint;

import java.time.LocalDateTime;

public record ComplaintHistoryResponse(
        Long id,
        ComplaintStatus fromStatus,
        ComplaintStatus toStatus,
        String toStatusLabel,
        String changedByName,       // 신청인이 직접 접수했으면 null
        LocalDateTime changedAt,
        String memo
) {
    public static ComplaintHistoryResponse from(ComplaintStatusHistory h) {
        return new ComplaintHistoryResponse(
                h.getId(),
                h.getFromStatus(),
                h.getToStatus(),
                h.getToStatus().getLabel(),
                h.getChangedBy() == null ? null : h.getChangedBy().getName(),
                h.getChangedAt(),
                h.getMemo()
        );
    }
}
