-- ==============================
-- 초기 계정 데이터
-- ==============================

-- ADMIN 계정 (email: admin@malgn.com / password: admin123)
insert into members (email, username, nickname, password, role)
values ('admin@malgn.com', '관리자', 'admin', '$2a$10$OpuG6DDrjsoO.F5cl9v7eO2eFbUe4AgE.dauLGfSZVOO9k0yLHTZy', 'ADMIN');

-- USER 계정 (email: user@malgn.com / password: user123)
insert into members (email, username, nickname, password, role)
values ('user@malgn.com', '사용자', 'user', '$2a$10$l6gIyllChJEHmnMJo/5DlOXn7hzDj7stuMf2P2hv0o8CiY26rFTpO', 'USER');