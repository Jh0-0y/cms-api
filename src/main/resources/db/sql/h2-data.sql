-- ==============================
-- 초기 계정 데이터
-- ==============================

-- ADMIN 계정 (email: admin@malgn.com / password: admin123)
insert into members (email, username, nickname, password, role)
values ('admin@malgn.com', '관리자', 'admin', '$2a$10$3/ypADLMWAsBMCbbbYcYVO/lQJbF5x.AoZbvaomqPWwMjUdS2CJJG', 'ADMIN');

-- USER 계정 (email: user@malgn.com / password: user123)
insert into members (email, username, nickname, password, role)
values ('user@malgn.com', '사용자', 'user', '$2a$10$LoaRyMZ2z4oGUWlE6/4Kp.6HovHeQ7ZiDUJHRSyBmpgMWbWjGwY7W', 'USER');