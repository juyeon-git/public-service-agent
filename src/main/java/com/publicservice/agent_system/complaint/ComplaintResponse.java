package com.publicservice.agent_system.complaint;

import java.time.LocalDateTime;

public record ComplaintResponse(
        Long id,
        String title,
        String content,
        ComplaintStatus status,
        String statusLabel,
        String applicantName,
        String serviceTypeName,
        String departmentName,
        String assigneeName,
        LocalDateTime receivedAt,
        LocalDateTime dueAt,
        LocalDateTime completedAt,
        boolean delayed
) {
    public static ComplaintResponse from(Complaint c, LocalDateTime now) {
        return new ComplaintResponse(
                c.getId(),
                c.getTitle(),
                c.getContent(),
                c.getStatus(),
                c.getStatus().getLabel(),
                c.getApplicant().getName(),
                c.getServiceType().getName(),
                c.getDepartment() == null ? null : c.getDepartment().getName(),   // 미배정이면 null
                c.getAssignee() == null ? null : c.getAssignee().getName(),       // 미배정이면 null
                c.getReceivedAt(),
                c.getDueAt(),
                c.getCompletedAt(),
                c.isDelayed(now)
        );
    }
}
