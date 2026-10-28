package com.publicservice.agent_system.complaint;

import com.publicservice.agent_system.employee.Employee;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "complaint_status_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ComplaintStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "complaint_id", nullable = false)
    private Complaint complaint;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", length = 20)          // 첫 접수 이력은 이전 상태가 없음
    private ComplaintStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, length = 20)
    private ComplaintStatus toStatus;

    @ManyToOne(fetch = FetchType.LAZY)                    // 신청인이 직접 접수하면 직원이 없음
    @JoinColumn(name = "changed_by")
    private Employee changedBy;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @Column(length = 500)
    private String memo;

    public static ComplaintStatusHistory record(Complaint complaint, ComplaintStatus fromStatus,
                                                ComplaintStatus toStatus, Employee changedBy,
                                                LocalDateTime changedAt, String memo) {
        ComplaintStatusHistory history = new ComplaintStatusHistory();
        history.complaint = complaint;
        history.fromStatus = fromStatus;
        history.toStatus = toStatus;
        history.changedBy = changedBy;
        history.changedAt = changedAt;
        history.memo = memo;
        return history;
    }
}
