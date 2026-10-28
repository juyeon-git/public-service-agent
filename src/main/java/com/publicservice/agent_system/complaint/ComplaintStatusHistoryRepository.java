package com.publicservice.agent_system.complaint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ComplaintStatusHistoryRepository extends JpaRepository<ComplaintStatusHistory, Long> {

    @Query("""
            select h from ComplaintStatusHistory h
            left join fetch h.changedBy
            where h.complaint.id = :complaintId
            order by h.changedAt asc, h.id asc
            """)
    List<ComplaintStatusHistory> findByComplaintId(@Param("complaintId") Long complaintId);
}
