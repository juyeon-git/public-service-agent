package com.publicservice.agent_system.statistics;

public record DelayStatResponse(
        String name,
        long totalCount,
        long delayedCount,
        double delayRate,            // 지연율(%) 소수 첫째 자리
        Double avgProcessingDays     // 완료 건 평균 처리 일수, 완료 건이 없으면 null
) {
    public static DelayStatResponse from(DelayStatRow row) {
        long total = row.getTotalCount();
        long delayed = row.getDelayedCount();
        double rate = total == 0 ? 0.0 : Math.round(delayed * 1000.0 / total) / 10.0;
        Double avgDays = row.getAvgProcessingDays() == null
                ? null
                : Math.round(row.getAvgProcessingDays() * 10) / 10.0;
        return new DelayStatResponse(row.getName(), total, delayed, rate, avgDays);
    }
}
