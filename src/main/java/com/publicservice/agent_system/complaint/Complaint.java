package com.publicservice.agent_system.complaint;

import com.publicservice.agent_system.applicant.Applicant;
import com.publicservice.agent_system.department.Department;
import com.publicservice.agent_system.employee.Employee;
import com.publicservice.agent_system.global.exception.ConflictException;
import com.publicservice.agent_system.servicetype.ServiceType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;

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

    @Column(name = "updated_at", nullable = false, insertable = false)   // 처음엔 DB DEFAULT, 이후 상태 변경 때 갱신
    private LocalDateTime updatedAt;

    // ===== 생성: 민원 접수 =====

    /** 민원 접수. 기한(due_at)은 접수 시점에 계산해서 고정(스냅샷) */
    public static Complaint receive(Applicant applicant, ServiceType serviceType,
                                    String title, String content, LocalDateTime now) {
        Complaint complaint = new Complaint();
        complaint.applicant = applicant;
        complaint.serviceType = serviceType;
        complaint.title = title;
        complaint.content = content;
        complaint.status = ComplaintStatus.RECEIVED;
        complaint.receivedAt = now;
        complaint.dueAt = now.plusDays(serviceType.getSlaDays());
        return complaint;
    }

    // ===== 상태 변경: 정해진 순서로만 바뀌도록 Entity가 직접 검사 =====

    /** 접수(또는 배정) → 배정: 담당자를 정하면 부서도 담당자의 부서로 */
    public void assign(Employee assignee, LocalDateTime now) {
        requireStatus(ComplaintStatus.RECEIVED, ComplaintStatus.ASSIGNED);
        this.assignee = assignee;
        this.department = assignee.getDepartment();
        this.status = ComplaintStatus.ASSIGNED;
        this.updatedAt = now;
    }

    /** 배정 → 처리중 */
    public void startProgress(LocalDateTime now) {
        requireStatus(ComplaintStatus.ASSIGNED);
        this.status = ComplaintStatus.IN_PROGRESS;
        this.updatedAt = now;
    }

    /** 처리중 → 완료 */
    public void complete(LocalDateTime now) {
        requireStatus(ComplaintStatus.IN_PROGRESS);
        this.status = ComplaintStatus.COMPLETED;
        this.completedAt = now;
        this.updatedAt = now;
    }

    /** 완료/반려 전이면 언제든 반려 가능 */
    public void reject(LocalDateTime now) {
        requireStatus(ComplaintStatus.RECEIVED, ComplaintStatus.ASSIGNED, ComplaintStatus.IN_PROGRESS);
        this.status = ComplaintStatus.REJECTED;
        this.completedAt = now;
        this.updatedAt = now;
    }

    private void requireStatus(ComplaintStatus... allowed) {
        if (Arrays.stream(allowed).noneMatch(s -> s == this.status)) {
            throw new ConflictException(
                    "INVALID_STATUS_TRANSITION",
                    "현재 상태(" + this.status.getLabel() + ")에서는 이 작업을 할 수 없습니다. id=" + this.id);
        }
    }

    /** 처리 지연 여부: 완료됐으면 완료일 > 기한, 미완료면 지금 > 기한 */
    public boolean isDelayed(LocalDateTime now) {
        if (completedAt != null) {
            return completedAt.isAfter(dueAt);
        }
        return now.isAfter(dueAt);
    }
}
