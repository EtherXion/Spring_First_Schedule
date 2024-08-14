package com.sparta.first_spring.controller;

import com.sparta.first_spring.dto.ScheduleRequestDto;
import com.sparta.first_spring.dto.ScheduleResponseDto;
import com.sparta.first_spring.entity.Schedule;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class Schedule_Controller {

    private final Map<Long, Schedule> scheduleList = new HashMap<>();

    @PostMapping("/schedule")
    public ScheduleResponseDto schedule(@RequestBody ScheduleRequestDto requestDto) {
        Schedule schedule = new Schedule(requestDto);

        // ------
        // jdbc 설정
        long maxId = scheduleList.size() > 0 ? Collections.max(scheduleList.keySet()) + 1 : 1;
        schedule.setId((int) maxId); // jdbc 이전 임시 id long으로 변경?
        scheduleList.put((long) schedule.getId(),schedule);

        // -------

        ScheduleResponseDto scheduleResponseDto = new ScheduleResponseDto(schedule);

        return scheduleResponseDto;

    }

    @GetMapping("/schedule")
    public List<ScheduleResponseDto> getSchedules() {
        List<ScheduleResponseDto> responseList = scheduleList.values().stream()
                .map(ScheduleResponseDto::new).toList();

        return responseList;
    }

    @PutMapping("/schedule/{id}")
    public long updateSchedule(@PathVariable long id, @RequestBody ScheduleRequestDto requestDto) {
        if (scheduleList.containsKey(id)) {
            Schedule schedule = scheduleList.get(id);

            schedule.update(requestDto);
            return schedule.getId();
        } else {
            throw new IllegalArgumentException("Schedule not found");
        }
    }

    @DeleteMapping("/schedule/{id}")
    public long deleteSchedule(@PathVariable long id) {
        if (scheduleList.containsKey(id)) {
            scheduleList.remove(id);
            return id;
        } else {
            throw new IllegalArgumentException("Schedule not found");
        }
    }



}
