package com.sparta.first_spring.entity;

import com.sparta.first_spring.dto.ScheduleRequestDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor

public class Schedule {
    private int id;
    private String todo;
    private String manager;
    private String password;
    private String date;
    private String time;

    public Schedule(ScheduleRequestDto requestDto) {
        this.todo = requestDto.getTodo();
        this.manager = requestDto.getManager();
        this.password = requestDto.getPassword();
        this.date = requestDto.getDate();
        this.time = requestDto.getTime();
    }

    public void update(ScheduleRequestDto requestDto) {
        this.todo = requestDto.getTodo();
        this.manager = requestDto.getManager();
        // 수정일로 바뀌려면 date time 도 필요?
    }

}
