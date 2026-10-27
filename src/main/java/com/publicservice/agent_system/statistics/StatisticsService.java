package com.publicservice.agent_system.statistics;

import com.publicservice.agent_system.complaint.ComplaintRepository;
import com.publicservice.agent_system.complaint.ComplaintResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;
    private final ComplaintRepository complaintRepository;

    public DelayStatisticsResponse delaysByDepartment(LocalDate from, LocalDate to) {
        StatisticsPeriod period = StatisticsPeriod.of(from, to);
        List<DelayStatResponse> items = statisticsRepository
                .findDelayStatsByDepartment(period.fromDateTime(), period.toDateTime(), LocalDateTime.now())
                .stream()
                .map(DelayStatResponse::from)
                .toList();
        return new DelayStatisticsResponse(period.from(), period.to(), items);
    }

    public DelayStatisticsResponse delaysByServiceType(LocalDate from, LocalDate to) {
        StatisticsPeriod period = StatisticsPeriod.of(from, to);
        List<DelayStatResponse> items = statisticsRepository
                .findDelayStatsByServiceType(period.fromDateTime(), period.toDateTime(), LocalDateTime.now())
                .stream()
                .map(DelayStatResponse::from)
                .toList();
        return new DelayStatisticsResponse(period.from(), period.to(), items);
    }

    public List<ComplaintResponse> overdueComplaints() {
        LocalDateTime now = LocalDateTime.now();
        return complaintRepository.findOverdue(now)
                .stream()
                .map(c -> ComplaintResponse.from(c, now))
                .toList();
    }
}
