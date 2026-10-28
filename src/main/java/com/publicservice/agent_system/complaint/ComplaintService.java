package com.publicservice.agent_system.complaint;

import com.publicservice.agent_system.applicant.Applicant;
import com.publicservice.agent_system.applicant.ApplicantRepository;
import com.publicservice.agent_system.employee.Employee;
import com.publicservice.agent_system.employee.EmployeeRepository;
import com.publicservice.agent_system.global.exception.BadRequestException;
import com.publicservice.agent_system.global.exception.ConflictException;
import com.publicservice.agent_system.global.exception.NotFoundException;
import com.publicservice.agent_system.servicetype.ServiceType;
import com.publicservice.agent_system.servicetype.ServiceTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)     // 기본은 조회 전용, 데이터를 바꾸는 메서드에만 @Transactional을 따로 붙임
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ComplaintStatusHistoryRepository historyRepository;
    private final ApplicantRepository applicantRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final EmployeeRepository employeeRepository;

    // ===================== 조회 =====================

    public List<ComplaintResponse> findAll(ComplaintStatus status) {
        List<Complaint> complaints = (status == null)
                ? complaintRepository.findAllWithDetails()
                : complaintRepository.findAllWithDetailsByStatus(status);

        LocalDateTime now = LocalDateTime.now();
        return complaints.stream()
                .map(c -> ComplaintResponse.from(c, now))
                .toList();
    }

    public ComplaintResponse findById(Long id) {
        return ComplaintResponse.from(getComplaint(id), LocalDateTime.now());
    }

    public List<ComplaintHistoryResponse> findHistories(Long id) {
        getComplaint(id);   // 없는 민원이면 404
        return historyRepository.findByComplaintId(id).stream()
                .map(ComplaintHistoryResponse::from)
                .toList();
    }

    // ===================== 접수 =====================

    @Transactional
    public ComplaintResponse create(ComplaintCreateRequest request) {
        Applicant applicant = applicantRepository.findById(request.applicantId())
                .orElseThrow(() -> new NotFoundException(
                        "APPLICANT_NOT_FOUND", "신청인을 찾을 수 없습니다. id=" + request.applicantId()));

        ServiceType serviceType = serviceTypeRepository.findById(request.serviceTypeId())
                .orElseThrow(() -> new NotFoundException(
                        "SERVICE_TYPE_NOT_FOUND", "업무 유형을 찾을 수 없습니다. id=" + request.serviceTypeId()));
        if (!serviceType.getActive()) {
            throw new BadRequestException("SERVICE_TYPE_INACTIVE", "사용이 중지된 업무 유형입니다. id=" + serviceType.getId());
        }

        LocalDateTime now = LocalDateTime.now();
        Complaint complaint = Complaint.receive(applicant, serviceType, request.title(), request.content(), now);

        // ① 민원 저장 + ② 접수 이력 저장 → 하나의 트랜잭션 (둘 다 성공하거나 둘 다 취소)
        complaintRepository.save(complaint);
        historyRepository.save(ComplaintStatusHistory.record(
                complaint, null, ComplaintStatus.RECEIVED, null, now, "접수"));

        return ComplaintResponse.from(complaint, now);
    }

    // ===================== 상태 변경 =====================

    @Transactional
    public ComplaintResponse changeStatus(Long id, ComplaintStatusChangeRequest request) {
        Complaint complaint = getComplaint(id);
        Employee changedBy = getEmployee(request.employeeId());

        ComplaintStatus fromStatus = complaint.getStatus();
        LocalDateTime now = LocalDateTime.now();

        switch (request.toStatus()) {
            case ASSIGNED -> {
                if (request.assigneeId() == null) {
                    throw new BadRequestException("ASSIGNEE_REQUIRED", "배정하려면 담당자(assigneeId)가 필요합니다.");
                }
                complaint.assign(getEmployee(request.assigneeId()), now);
            }
            case IN_PROGRESS -> complaint.startProgress(now);
            case COMPLETED -> complaint.complete(now);
            case REJECTED -> {
                if (request.memo() == null || request.memo().isBlank()) {
                    throw new BadRequestException("REJECT_REASON_REQUIRED", "반려할 때는 사유(memo)가 필요합니다.");
                }
                complaint.reject(now);
            }
            case RECEIVED -> throw new ConflictException(
                    "INVALID_STATUS_TRANSITION", "접수 상태로는 되돌릴 수 없습니다.");
        }

        // complaint는 save()를 다시 부르지 않아도 됨: 트랜잭션이 끝날 때 바뀐 값이 자동으로 UPDATE (변경 감지)
        historyRepository.save(ComplaintStatusHistory.record(
                complaint, fromStatus, request.toStatus(), changedBy, now, request.memo()));

        return ComplaintResponse.from(complaint, now);
    }

    // ===================== 내부 =====================

    private Complaint getComplaint(Long id) {
        return complaintRepository.findWithDetailsById(id)
                .orElseThrow(() -> new NotFoundException(
                        "COMPLAINT_NOT_FOUND", "민원을 찾을 수 없습니다. id=" + id));
    }

    private Employee getEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "EMPLOYEE_NOT_FOUND", "직원을 찾을 수 없습니다. id=" + id));
    }
}
