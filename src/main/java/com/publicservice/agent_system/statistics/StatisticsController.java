package com.publicservice.agent_system.statistics;

import com.publicservice.agent_system.complaint.ComplaintResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /** 부서별 지연 통계. 기간 생략 시 지난달 */
    @GetMapping("/delays/by-department")
    public DelayStatisticsResponse delaysByDepartment(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return statisticsService.delaysByDepartment(from, to);
    }

    /** 업무 유형별 지연 통계. 기간 생략 시 지난달 */
    @GetMapping("/delays/by-service-type")
    public DelayStatisticsResponse delaysByServiceType(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return statisticsService.delaysByServiceType(from, to);
    }

    /** 지금 기준으로 기한을 넘긴 미완료 민원 */
    @GetMapping("/overdue")
    public List<ComplaintResponse> overdueComplaints() {
        return statisticsService.overdueComplaints();
    }
}
