package com.publicservice.agent_system.complaint;

import com.publicservice.agent_system.applicant.Applicant;
import com.publicservice.agent_system.department.Department;
import com.publicservice.agent_system.employee.Employee;
import com.publicservice.agent_system.servicetype.ServiceType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "complaint")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "complaint_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "applicant_id", nullable = false)
    private Applicant applicant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_type_id", nullable = false)
    private ServiceType serviceType;

    @ManyToOne(fetch = FetchType.LAZY)              // 배정 전에는 없음 (NULL 허용)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)              // 배정 전에는 없음 (NULL 허용)
    @JoinColumn(name = "assignee_id")
    private Employee assignee;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)                    // DB에 "COMPLETED" 같은 문자열로 저장
    @Column(nullable = false, length = 20)
    private ComplaintStatus status;

    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;

    @Column(name = "due_at", nullable = false)
    private LocalDateTime dueAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /** 처리 지연 여부: 완료됐으면 완료일 > 기한, 미완료면 지금 > 기한 */
    public boolean isDelayed(LocalDateTime now) {
        if (completedAt != null) {
            return completedAt.isAfter(dueAt);
        }
        return now.isAfter(dueAt);
    }
}
