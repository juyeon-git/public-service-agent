package com.publicservice.agent_system.statistics;

import com.publicservice.agent_system.global.exception.BadRequestException;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 통계 조회 기간. from 이상 ~ to 미만 (to는 포함하지 않음).
 * 예: from=2026-09-01, to=2026-10-01 → 9월 한 달
 */
public record StatisticsPeriod(LocalDate from, LocalDate to) {

    public static StatisticsPeriod of(LocalDate from, LocalDate to) {
        if (from == null && to == null) {               // 기간을 안 주면 "지난달"
            LocalDate firstDayOfThisMonth = LocalDate.now().withDayOfMonth(1);
            return new StatisticsPeriod(firstDayOfThisMonth.minusMonths(1), firstDayOfThisMonth);
        }
        if (from == null || to == null) {
            throw new BadRequestException("INVALID_PERIOD", "from과 to는 함께 지정해야 합니다.");
        }
        if (!from.isBefore(to)) {
            throw new BadRequestException("INVALID_PERIOD", "from은 to보다 이전 날짜여야 합니다.");
        }
        return new StatisticsPeriod(from, to);
    }

    public LocalDateTime fromDateTime() {
        return from.atStartOfDay();
    }

    public LocalDateTime toDateTime() {
        return to.atStartOfDay();
    }
}
