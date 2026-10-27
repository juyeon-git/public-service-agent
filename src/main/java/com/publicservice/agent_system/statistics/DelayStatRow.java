package com.publicservice.agent_system.statistics;

/**
 * 통계 SQL 결과 한 줄을 받는 그릇 (인터페이스 프로젝션).
 * SQL의 별칭("name", "totalCount" ...)과 getter 이름이 짝을 이룸.
 */
public interface DelayStatRow {

    String getName();

    Long getTotalCount();

    Long getDelayedCount();

    Double getAvgProcessingDays();
}
