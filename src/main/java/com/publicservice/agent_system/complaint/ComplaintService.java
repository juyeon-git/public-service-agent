package com.publicservice.agent_system.complaint;

import com.publicservice.agent_system.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComplaintService {

    private final ComplaintRepository complaintRepository;

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
        Complaint complaint = complaintRepository.findWithDetailsById(id)
                .orElseThrow(() -> new NotFoundException(
                        "COMPLAINT_NOT_FOUND",
                        "민원을 찾을 수 없습니다. id=" + id));
        return ComplaintResponse.from(complaint, LocalDateTime.now());
    }
}
