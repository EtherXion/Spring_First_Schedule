package com.sparta.first_spring.entity;

import com.sparta.first_spring.dto.ScheduleRequestDto;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor

public class Schedule {
    private long id;
    private String todo;
    private String manager;
    private String password;
    private Timestamp date; // 시간 종류가 여럿인 듯
    private Timestamp modify_date;

    public Schedule(ScheduleRequestDto requestDto) {
        this.todo = requestDto.getTodo();
        this.manager = requestDto.getManager();
        this.password = requestDto.getPassword();
        this.date = requestDto.getDate();
        this.modify_date = requestDto.getModify_date();
    }

    public void update(ScheduleRequestDto requestDto) {
        this.todo = requestDto.getTodo();
        this.manager = requestDto.getManager();
        // 수정일로 바뀌려면 date time 도 필요?
    }

}
