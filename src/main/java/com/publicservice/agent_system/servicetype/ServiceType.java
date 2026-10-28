package com.publicservice.agent_system.servicetype;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "service_type")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ServiceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_type_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "sla_days", nullable = false)
    private Integer slaDays;

    @Column(name = "is_active", nullable = false)
    private Boolean active;     // 비활성 업무 유형으로는 새 민원을 접수할 수 없음
}
