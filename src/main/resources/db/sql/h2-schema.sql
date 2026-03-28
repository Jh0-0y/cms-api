-- ==============================
-- Member
-- ==============================
create table members
(
    id                 bigint primary key not null auto_increment,
    email              varchar(100)       not null unique,
    username           varchar(50)        not null,
    nickname           varchar(50)        not null unique,
    password           varchar(255)       not null,
    role               varchar(10)        not null,
    created_date       timestamp default now(),
    last_modified_date timestamp
);

-- ==============================
-- Refresh Token
-- ==============================
create table refresh_tokens
(
    id        bigint primary key not null auto_increment,
    token     varchar(255)       not null unique,
    member_id bigint,
    constraint fk_refresh_tokens_member foreign key (member_id) references members (id)
);

-- ==============================
-- Content
-- ==============================
create table contents
(
    id                 bigint primary key not null auto_increment,
    title              varchar(100)       not null,
    description        text,
    view_count         bigint             not null default 0,
    is_deleted         boolean            not null default false,
    created_date       timestamp default now(),
    created_by         varchar(50)        not null,
    last_modified_date timestamp,
    last_modified_by   varchar(50),
    member_id          bigint             not null,
    constraint fk_contents_member foreign key (member_id) references members (id)
);