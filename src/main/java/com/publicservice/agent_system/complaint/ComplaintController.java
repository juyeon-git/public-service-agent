package com.publicservice.agent_system.complaint;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    @GetMapping
    public List<ComplaintResponse> getComplaints(
            @RequestParam(required = false) ComplaintStatus status) {   // ?status=IN_PROGRESS (선택)
        return complaintService.findAll(status);
    }

    @GetMapping("/{id}")
    public ComplaintResponse getComplaint(@PathVariable Long id) {
        return complaintService.findById(id);
    }

    /** 상태 변경 이력 */
    @GetMapping("/{id}/histories")
    public List<ComplaintHistoryResponse> getHistories(@PathVariable Long id) {
        return complaintService.findHistories(id);
    }

    /** 민원 접수 → 201 Created */
    @PostMapping
    public ResponseEntity<ComplaintResponse> createComplaint(
            @Valid @RequestBody ComplaintCreateRequest request) {
        ComplaintResponse response = complaintService.create(request);
        return ResponseEntity
                .created(URI.create("/api/complaints/" + response.id()))
                .body(response);
    }

    /** 민원 상태 변경 (배정, 처리중, 완료, 반려) */
    @PatchMapping("/{id}/status")
    public ComplaintResponse changeStatus(@PathVariable Long id,
                                          @Valid @RequestBody ComplaintStatusChangeRequest request) {
        return complaintService.changeStatus(id, request);
    }
}
