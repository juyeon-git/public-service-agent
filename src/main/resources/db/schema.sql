-- =========================================
-- 공공서비스 민원 처리 시스템 - 초기 스키마
-- =========================================

-- 1. 부서
CREATE TABLE department (
                            department_id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                            name            VARCHAR(50)  NOT NULL UNIQUE,
                            created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. 직원 (= 시스템 사용자)
CREATE TABLE employee (
                          employee_id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          department_id   BIGINT       NOT NULL REFERENCES department (department_id),
                          login_id        VARCHAR(50)  NOT NULL UNIQUE,
                          password_hash   VARCHAR(100) NOT NULL,
                          name            VARCHAR(50)  NOT NULL,
                          role            VARCHAR(20)  NOT NULL CHECK (role IN ('STAFF', 'ADMIN')),
                          created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. 신청인
CREATE TABLE applicant (
                           applicant_id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                           name            VARCHAR(50)  NOT NULL,
                           phone           VARCHAR(20),
                           email           VARCHAR(100),
                           created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 4. 업무 유형
CREATE TABLE service_type (
                              service_type_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                              department_id   BIGINT       NOT NULL REFERENCES department (department_id),
                              name            VARCHAR(100) NOT NULL UNIQUE,
                              description     TEXT,
                              sla_days        INTEGER      NOT NULL CHECK (sla_days > 0),
                              is_active       BOOLEAN      NOT NULL DEFAULT TRUE
);

-- 5. 민원
CREATE TABLE complaint (
                           complaint_id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                           applicant_id    BIGINT       NOT NULL REFERENCES applicant (applicant_id),
                           service_type_id BIGINT       NOT NULL REFERENCES service_type (service_type_id),
                           department_id   BIGINT       REFERENCES department (department_id),
                           assignee_id     BIGINT       REFERENCES employee (employee_id),
                           title           VARCHAR(200) NOT NULL,
                           content         TEXT         NOT NULL,
                           status          VARCHAR(20)  NOT NULL DEFAULT 'RECEIVED'
                               CHECK (status IN ('RECEIVED', 'ASSIGNED', 'IN_PROGRESS', 'COMPLETED', 'REJECTED')),
                           received_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           due_at          TIMESTAMP    NOT NULL,
                           completed_at    TIMESTAMP,
                           created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           CHECK (due_at >= received_at),
                           CHECK (completed_at IS NULL OR completed_at >= received_at)
);

-- 6. 민원 상태 변경 이력
CREATE TABLE complaint_status_history (
                                          history_id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                          complaint_id    BIGINT       NOT NULL REFERENCES complaint (complaint_id),
                                          from_status     VARCHAR(20),
                                          to_status       VARCHAR(20)  NOT NULL
                                              CHECK (to_status IN ('RECEIVED', 'ASSIGNED', 'IN_PROGRESS', 'COMPLETED', 'REJECTED')),
                                          changed_by      BIGINT       REFERENCES employee (employee_id),
                                          changed_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                          memo            VARCHAR(500)
);

-- 테이블 설명 (DBeaver, ERD에 표시됨)
COMMENT ON TABLE department               IS '부서';
COMMENT ON TABLE employee                 IS '직원(시스템 사용자)';
COMMENT ON TABLE applicant                IS '민원 신청인';
COMMENT ON TABLE service_type             IS '업무 유형';
COMMENT ON TABLE complaint                IS '민원';
COMMENT ON TABLE complaint_status_history IS '민원 상태 변경 이력';