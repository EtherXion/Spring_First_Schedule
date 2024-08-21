package com.sparta.first_spring.service;

import com.sparta.first_spring.dto.ScheduleRequestDto;
import com.sparta.first_spring.dto.ScheduleResponseDto;
import com.sparta.first_spring.entity.Schedule;
import com.sparta.first_spring.repository.ScheduleRepository;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class ScheduleService {

    private final JdbcTemplate jdbcTemplate;

    public ScheduleService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ScheduleResponseDto createSchedule(ScheduleRequestDto requestDto) {
        // RequestDto - > Entuty
        Schedule schedule = new Schedule(requestDto);

        // DB 저장
        ScheduleRepository scheduleRepository  = new ScheduleRepository(jdbcTemplate);

        Schedule saveSchedule = scheduleRepository.save(schedule);

        // Entity - > ResponseDto
        ScheduleResponseDto scheduleResponseDto = new ScheduleResponseDto(saveSchedule);

        return scheduleResponseDto;

    }

    public List<ScheduleResponseDto> getAllSchedules() {
        // DB 전체 조회
        ScheduleRepository scheduleRepository = new ScheduleRepository(jdbcTemplate);
        return scheduleRepository.findAll();
    }

    public ScheduleResponseDto getSchedules(Long id) {

        ScheduleRepository scheduleRepository = new ScheduleRepository(jdbcTemplate);

        Schedule schedule = scheduleRepository.findById(id);

        if(schedule != null) {
            scheduleRepository.find(id);

            return scheduleRepository.find(id); // 여기에 id 필요한게?
        } else {
            throw new IllegalArgumentException("일정 없음");
        }



    }

    public Long updateSchedule(Long id , ScheduleRequestDto requestDto) {
        ScheduleRepository scheduleRepository = new ScheduleRepository(jdbcTemplate);

        Schedule schedule = scheduleRepository.findById(id);

        if(schedule != null) {
            scheduleRepository.update(id , requestDto);

            return id;
        } else {
            throw new IllegalArgumentException("일정 없음");
        }


    }

    public Long deleteSchedule(Long id , ScheduleRequestDto requestDto) {
        ScheduleRepository scheduleRepository = new ScheduleRepository(jdbcTemplate);

        Schedule schedule = scheduleRepository.findById(id);

        if(schedule != null) {
            scheduleRepository.delete(id , requestDto);

            return id;
        } else {
            throw new IllegalArgumentException("일정 없음");
        }
    }



}
