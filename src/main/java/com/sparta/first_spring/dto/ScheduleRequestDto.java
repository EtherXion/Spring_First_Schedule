package com.sparta.first_spring.dto;

import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class ScheduleRequestDto {
    private String todo;
    private String manager;
    private String password;
    private Timestamp date; // 시간 담는 형식? timestamp , datetime 2중류
    private Timestamp modify_date;

}
