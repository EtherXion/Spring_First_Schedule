package com.sparta.first_spring.dto;

import com.sparta.first_spring.entity.Schedule;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class ScheduleResponseDto {
    private long id;
    private String todo;
    private String manager;
    private Timestamp date;
    private String modify_date;

    public ScheduleResponseDto(Schedule schedule){
        this.id = schedule.getId();
        this.todo = schedule.getTodo();
        this.manager = schedule.getManager();
        this.date = schedule.getDate(); // LocalDateTime.now() ?
        this.modify_date = schedule.getModify_date();
    }

    public ScheduleResponseDto(Long id, String todo, String manager, Timestamp date, String modify_date){
        this.id = Math.toIntExact(id);
        this.todo = todo;
        this.manager = manager;
        this.date = date;
        this.modify_date = modify_date;
    }


}
