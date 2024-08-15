package com.sparta.first_spring.entity;

import com.sparta.first_spring.dto.ScheduleRequestDto;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor

public class Schedule {
    private long id;
    private String todo;
    private String manager;
    private String password;

    // 아예 여기서 현재 시간을 받아오고 입력창에서는 시간 관련 안건드리는 편이?
    private Timestamp date; // 시간 종류가 여럿인 듯
    private Timestamp modify_date;

    public Schedule(ScheduleRequestDto requestDto) {
        this.todo = requestDto.getTodo();
        this.manager = requestDto.getManager();
        this.password = requestDto.getPassword();
        this.date = Timestamp.valueOf(LocalDateTime.now()); // 아예 현재 시간 받아오도록
        this.modify_date = Timestamp.valueOf(LocalDateTime.now());
    }

    public void update(ScheduleRequestDto requestDto) {
        this.todo = requestDto.getTodo();
        this.manager = requestDto.getManager();

        // 바꾸면서 추가된 부분 이거 없어도 되나?
        this.modify_date = Timestamp.valueOf(LocalDateTime.now());

        // 수정일로 바뀌려면 date time 도 필요?
    }

}
