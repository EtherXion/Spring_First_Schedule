create table schedule.schedule_table
(
    id          int auto_increment comment '일정 id'
        primary key,
    todo        varchar(100) not null comment '할 일',
    manager     varchar(100) not null comment '담당자명',
    password    varchar(100) not null comment '비밀번호',
    date        datetime     not null comment '작성/수정일',
    modify_date datetime     not null comment '작성/수정시간'
);
