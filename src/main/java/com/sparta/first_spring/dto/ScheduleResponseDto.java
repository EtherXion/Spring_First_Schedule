package com.sparta.first_spring.dto;

import com.sparta.first_spring.entity.Schedule;
import lombok.Getter;

@Getter
public class ScheduleResponseDto {
    private int id;
    private String todo;
    private String manager;
    private String date;
    private String time;

    public ScheduleResponseDto(Schedule schedule){
        this.id = schedule.getId();
        this.todo = schedule.getTodo();
        this.manager = schedule.getManager();
        this.date = schedule.getDate();
        this.time = schedule.getTime();
    }

    public ScheduleResponseDto(Long id, String todo, String manager, String date, String time){
        this.id = Math.toIntExact(id);
        this.todo = todo;
        this.manager = manager;
        this.date = date;
        this.time = time;
    }


}
