package com.publicservice.agent_system.complaint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    @Query("""
            select c from Complaint c
            join fetch c.applicant
            join fetch c.serviceType
            left join fetch c.department
            left join fetch c.assignee
            order by c.receivedAt desc
            """)
    List<Complaint> findAllWithDetails();

    @Query("""
            select c from Complaint c
            join fetch c.applicant
            join fetch c.serviceType
            left join fetch c.department
            left join fetch c.assignee
            where c.status = :status
            order by c.receivedAt desc
            """)
    List<Complaint> findAllWithDetailsByStatus(@Param("status") ComplaintStatus status);

    @Query("""
            select c from Complaint c
            join fetch c.applicant
            join fetch c.serviceType
            left join fetch c.department
            left join fetch c.assignee
            where c.id = :id
            """)
    Optional<Complaint> findWithDetailsById(@Param("id") Long id);

    /** 기한이 지났는데 아직 완료되지 않은 민원 (기한이 오래된 순) */
    @Query("""
            select c from Complaint c
            join fetch c.applicant
            join fetch c.serviceType
            left join fetch c.department
            left join fetch c.assignee
            where c.completedAt is null
              and c.dueAt < :now
            order by c.dueAt asc
            """)
    List<Complaint> findOverdue(@Param("now") LocalDateTime now);
}
