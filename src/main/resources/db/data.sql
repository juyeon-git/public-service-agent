-- =========================================
-- 공공서비스 민원 처리 시스템 - 테스트용 가상 데이터
-- (실존 기관·인물과 무관한 가상 데이터)
-- =========================================

-- 0. 기존 데이터 비우고 번호(IDENTITY)도 1부터 다시 시작
TRUNCATE complaint_status_history, complaint, service_type, applicant, employee, department
    RESTART IDENTITY CASCADE;

-- 1. 부서
INSERT INTO department (name) VALUES
                                  ('장학지원부'),      -- 1
                                  ('보증심사부'),      -- 2
                                  ('도로관리부'),      -- 3
                                  ('고객지원부');      -- 4

-- 2. 직원 (password_hash는 로그인 기능 전까지 쓰는 임시값)
INSERT INTO employee (department_id, login_id, password_hash, name, role) VALUES
                                                                              (1, 'kimcs',  'temp-hash', '김철수', 'STAFF'),   -- 1
                                                                              (1, 'leeyh',  'temp-hash', '이영희', 'STAFF'),   -- 2
                                                                              (2, 'parkms', 'temp-hash', '박민수', 'STAFF'),   -- 3
                                                                              (3, 'jungde', 'temp-hash', '정다은', 'STAFF'),   -- 4
                                                                              (4, 'choihw', 'temp-hash', '최현우', 'STAFF'),   -- 5
                                                                              (4, 'admin',  'temp-hash', '윤관리', 'ADMIN');   -- 6

-- 3. 신청인
INSERT INTO applicant (name, phone, email) VALUES
                                               ('홍길동', '010-0000-0001', 'user1@example.com'),   -- 1
                                               ('김민수', '010-0000-0002', 'user2@example.com'),   -- 2
                                               ('이서연', '010-0000-0003', NULL),                  -- 3
                                               ('박지훈', '010-0000-0004', 'user4@example.com'),   -- 4
                                               ('최유진', '010-0000-0005', NULL),                  -- 5
                                               ('정수아', '010-0000-0006', 'user6@example.com');   -- 6

-- 4. 업무 유형
INSERT INTO service_type (department_id, name, description, sla_days) VALUES
                                                                          (1, '장학금 신청',        '국가장학금 등 장학금 신청 접수',     7),   -- 1
                                                                          (1, '학자금 대출 상담',    '학자금 대출 및 상환 관련 상담',      5),   -- 2
                                                                          (2, '보증 상담',          '창업·운영자금 보증 상담',          10),   -- 3
                                                                          (3, '도로 파손 신고',      '포트홀, 낙하물, 시설물 파손 신고',    3),   -- 4
                                                                          (4, '전기요금 이의신청',    '요금 과다 청구 등 이의신청',        14);   -- 5

-- 5. 민원 (2026년 9월~10월, 처리 지연/정상/진행중/반려 섞음)
INSERT INTO complaint
(applicant_id, service_type_id, department_id, assignee_id, title, content, status, received_at, due_at, completed_at)
VALUES
    (1, 1, 1, 1,    '국가장학금 신청',          '2학기 국가장학금 신청합니다.',        'COMPLETED',   '2026-09-01 09:00', '2026-09-08 09:00', '2026-09-10 15:00'),  -- 지연
    (2, 1, 1, 2,    '장학금 서류 보완 문의',     '제출 서류 보완 방법 문의',          'COMPLETED',   '2026-09-03 10:00', '2026-09-10 10:00', '2026-09-07 11:00'),  -- 정상
    (3, 2, 1, 1,    '학자금 대출 상환 상담',     '상환 유예 가능 여부 문의',          'COMPLETED',   '2026-09-05 14:00', '2026-09-10 14:00', '2026-09-12 10:00'),  -- 지연
    (4, 3, 2, 3,    '창업 보증 상담 신청',       '창업 초기 운영자금 보증 상담 요청',   'COMPLETED',   '2026-09-08 09:30', '2026-09-18 09:30', '2026-09-16 17:00'),  -- 정상
    (5, 4, 3, 4,    '고속도로 포트홀 신고',      '상행선 2차로 포트홀 발견',          'COMPLETED',   '2026-09-10 08:00', '2026-09-13 08:00', '2026-09-15 13:00'),  -- 지연
    (6, 4, 3, 4,    '갓길 낙하물 신고',         '갓길에 적재물 낙하',               'COMPLETED',   '2026-09-15 07:00', '2026-09-18 07:00', '2026-09-16 09:00'),  -- 정상
    (1, 5, 4, 5,    '전기요금 과다 청구 이의신청', '전월 대비 요금이 3배 청구됨',        'IN_PROGRESS', '2026-09-20 11:00', '2026-10-04 11:00', NULL),                -- 기한 초과 미처리
    (2, 3, 2, 3,    '보증 한도 문의',           '추가 보증 한도 문의',              'IN_PROGRESS', '2026-09-28 10:00', '2026-10-08 10:00', NULL),                -- 진행 중
    (3, 4, NULL, NULL, '도로 표지판 파손 신고',   '교차로 안내 표지판 파손',           'RECEIVED',    '2026-10-02 18:00', '2026-10-05 18:00', NULL),                -- 접수만 됨
    (4, 1, 1, 2,    '장학금 중복 신청 건',       '같은 학기 장학금 중복 신청',         'REJECTED',    '2026-09-25 09:00', '2026-10-02 09:00', '2026-09-26 14:00');  -- 반려

-- 6. 상태 변경 이력 (1번 민원: 홍길동 국가장학금 신청)
INSERT INTO complaint_status_history (complaint_id, from_status, to_status, changed_by, changed_at, memo) VALUES
                                                                                                              (1, NULL,          'RECEIVED',    NULL, '2026-09-01 09:00', '온라인 접수'),
                                                                                                              (1, 'RECEIVED',    'ASSIGNED',    6,    '2026-09-02 10:00', '장학지원부 김철수 배정'),
                                                                                                              (1, 'ASSIGNED',    'IN_PROGRESS', 1,    '2026-09-03 09:00', NULL),
                                                                                                              (1, 'IN_PROGRESS', 'COMPLETED',   1,    '2026-09-10 15:00', '장학금 지급 완료');