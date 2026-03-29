-- ==============================
-- 초기 계정 데이터
-- ==============================
-- ADMIN 계정 (email: admin@malgn.com / password: admin123)
insert into members (email, username, nickname, password, role)
values ('admin@malgn.com', '김관리', '관리자', '$2a$10$OpuG6DDrjsoO.F5cl9v7eO2eFbUe4AgE.dauLGfSZVOO9k0yLHTZy', 'ADMIN');

-- USER 계정 (email: user@malgn.com / password: user123)
insert into members (email, username, nickname, password, role)
values ('user@malgn.com', '김맑은', '맑은사원', '$2a$10$l6gIyllChJEHmnMJo/5DlOXn7hzDj7stuMf2P2hv0o8CiY26rFTpO', 'USER');

-- USER2 계정 (email: user2@malgn.com / password: user456)
insert into members (email, username, nickname, password, role)
values ('user2@malgn.com', '이하늘', '하늘사원', '$2a$10$zben/VIq5tju/wyNrV11d.2CSEcw3.Jq1AAHnOM6bdFUAPkUFFRHq', 'USER');

-- ==============================
-- 초기 콘텐츠 데이터
-- ==============================
insert into contents (title, description, created_by)
values ('공지사항', 'Spring Boot 기반 CMS API 서버가 정상적으로 실행되었습니다.', 1);

insert into contents (title, description, created_by)
values ('맑은 사원의 자기소개 ', '안녕하세요 저는 맑은사원입니다.', 2);

insert into contents (title, description, created_by)
values ('사용자 작성 게시글', '일반 사용자가 작성한 게시글입니다. 본인만 수정·삭제할 수 있습니다.', 2);

insert into contents (title, description, is_deleted, created_by)
values ('삭제된 게시글 (복구 테스트용)', '삭제된 게시글입니다. PATCH /api/contents/{id}/restore 로 복구할 수 있습니다.', true, 2);

insert into contents (title, description, created_by)
values ('하늘사원의 첫 콘텐츠', '두 번째 유저인 저 하늘사원입니다. 다른 유저 계정으로 수정·삭제를 시도해보세요.', 3);