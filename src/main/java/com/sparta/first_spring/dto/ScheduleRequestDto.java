package com.sparta.first_spring.dto;

import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class ScheduleRequestDto {
    private String todo;
    private String manager;
    private String password;
    private long id;

    // 원래 여기에 date , modify_date 있었음

}
