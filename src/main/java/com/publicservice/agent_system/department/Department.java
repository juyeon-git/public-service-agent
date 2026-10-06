package com.publicservice.agent_system.department;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity                                   // 이 클래스는 DB 테이블과 연결된 Entity
@Table(name = "department")               // 연결할 테이블 이름
@Getter                                   // Lombok: getId(), getName() 등 자동 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JPA가 객체를 만들 때 쓰는 기본 생성자
public class Department {

    @Id                                                   // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // 번호는 DB가 자동 생성 (IDENTITY)
    @Column(name = "department_id")
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String name;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;   // 값은 DB의 DEFAULT(현재 시각)가 채움
}