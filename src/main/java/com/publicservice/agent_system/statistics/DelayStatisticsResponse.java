package com.publicservice.agent_system.statistics;

import java.time.LocalDate;
import java.util.List;

/** 통계 결과 + 어떤 기간의 통계인지 (to는 포함하지 않음) */
public record DelayStatisticsResponse(
        LocalDate from,
        LocalDate to,
        List<DelayStatResponse> items
) {
}
