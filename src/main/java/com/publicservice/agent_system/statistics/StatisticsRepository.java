package com.publicservice.agent_system.statistics;

import com.publicservice.agent_system.complaint.Complaint;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticsRepository extends Repository<Complaint, Long> {

    /** 부서별 접수 건수, 지연 건수, 평균 처리 일수 */
    @Query(value = """
            SELECT d.name AS "name",
                   COUNT(*) AS "totalCount",
                   SUM(CASE WHEN (c.completed_at IS NOT NULL AND c.completed_at > c.due_at)
                              OR (c.completed_at IS NULL AND c.due_at < :now)
                            THEN 1 ELSE 0 END) AS "delayedCount",
                   CAST(AVG(CASE WHEN c.status = 'COMPLETED'
                                 THEN EXTRACT(EPOCH FROM (c.completed_at - c.received_at)) / 86400
                            END) AS DOUBLE PRECISION) AS "avgProcessingDays"
            FROM complaint c
            JOIN department d ON d.department_id = c.department_id
            WHERE c.received_at >= :from
              AND c.received_at <  :to
            GROUP BY d.name
            ORDER BY "delayedCount" DESC, d.name
            """, nativeQuery = true)
    List<DelayStatRow> findDelayStatsByDepartment(@Param("from") LocalDateTime from,
                                                  @Param("to") LocalDateTime to,
                                                  @Param("now") LocalDateTime now);

    /** 업무 유형별 접수 건수, 지연 건수, 평균 처리 일수 */
    @Query(value = """
            SELECT s.name AS "name",
                   COUNT(*) AS "totalCount",
                   SUM(CASE WHEN (c.completed_at IS NOT NULL AND c.completed_at > c.due_at)
                              OR (c.completed_at IS NULL AND c.due_at < :now)
                            THEN 1 ELSE 0 END) AS "delayedCount",
                   CAST(AVG(CASE WHEN c.status = 'COMPLETED'
                                 THEN EXTRACT(EPOCH FROM (c.completed_at - c.received_at)) / 86400
                            END) AS DOUBLE PRECISION) AS "avgProcessingDays"
            FROM complaint c
            JOIN service_type s ON s.service_type_id = c.service_type_id
            WHERE c.received_at >= :from
              AND c.received_at <  :to
            GROUP BY s.name
            ORDER BY "delayedCount" DESC, s.name
            """, nativeQuery = true)
    List<DelayStatRow> findDelayStatsByServiceType(@Param("from") LocalDateTime from,
                                                   @Param("to") LocalDateTime to,
                                                   @Param("now") LocalDateTime now);
}
