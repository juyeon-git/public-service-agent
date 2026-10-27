package com.publicservice.agent_system.complaint;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}
