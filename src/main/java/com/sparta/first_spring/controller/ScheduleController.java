package com.sparta.first_spring.controller;

import com.sparta.first_spring.dto.ScheduleRequestDto;
import com.sparta.first_spring.dto.ScheduleResponseDto;
import com.sparta.first_spring.entity.Schedule;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ScheduleController {


    private final JdbcTemplate jdbcTemplate;

    public ScheduleController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/schedule")
    public ScheduleResponseDto schedule(@RequestBody ScheduleRequestDto requestDto) {
        Schedule schedule = new Schedule(requestDto);

        KeyHolder keyHolder = new GeneratedKeyHolder(); // 기본키를 반환받기 위한 객체

        String sql = "INSERT INTO schedule_table (id, todo, manager, password, date, time) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update( con ->  {
                    PreparedStatement ps = con.prepareStatement(sql,
                            Statement.RETURN_GENERATED_KEYS);

                    ps.setLong(1, schedule.getId());
                    ps.setString(2, requestDto.getTodo());
                    ps.setString(3, requestDto.getManager());
                    ps.setString(4, requestDto.getPassword());
                    ps.setString(5, requestDto.getDate());
                    ps.setString(6, requestDto.getTime());
                    return ps;
                },
                keyHolder);

        // DB Insert 후 받아온 기본키 확인
        Long id = keyHolder.getKey().longValue();
        schedule.setId(Math.toIntExact(id)); // 나중에 id DB int에서 long으로 고치면 바꿀 것

        ScheduleResponseDto scheduleResponseDto = new ScheduleResponseDto(schedule);

        return scheduleResponseDto;

    }

    @GetMapping("/schedule")
    public List<ScheduleResponseDto> getSchedules() {

        String sql = "SELECT * FROM schedule_table";

        return jdbcTemplate.query(sql, new RowMapper<ScheduleResponseDto>() {
            @Override
            public ScheduleResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                // SQL 결과로 받아온 데이터 ScheduleResponseDto 타입으로 변환
                Long id = rs.getLong("id");
                String todo = rs.getString("todo");
                String manager = rs.getString("manager");
                String date = rs.getString("date");
                String time = rs.getString("time");
                return new ScheduleResponseDto(id, todo, manager, date, time);
            }
        });
    }

    @PutMapping("/schedule/{id}")
    public Long updateSchedule(@PathVariable Long id, @RequestBody ScheduleRequestDto requestDto) {
        // 해당 일정이 존재하는지
        Schedule schedule = findById(id);
        if (schedule != null){
            // 일정 내용 수정
            String sql = "UPDATE schedule_table SET todo = ?, manager = ?, date = ?, time = ? WHERE id = ?";
            jdbcTemplate.update(sql, requestDto.getTodo(), requestDto.getManager(), requestDto.getDate(), requestDto.getTime(), id);

            return id;
        } else {
            throw new IllegalArgumentException("Schedule not found");
        }
    }

    @DeleteMapping("/schedule/{id}")
    public Long deleteSchedule(@PathVariable Long id) {
        Schedule schedule = findById(id);
        if (schedule != null){
            String sql = "DELETE FROM schedule_table WHERE id = ?";
            jdbcTemplate.update(sql, id);

            return id;
        } else {
            throw new IllegalArgumentException("Schedule not found");
        }
    }

    private Schedule findById(Long id) {
        String sql = "SELECT * FROM schedule_table WHERE id = ?";

        return jdbcTemplate.query(sql, resultSet ->{
            if (resultSet.next()){
                Schedule schedule = new Schedule();
                schedule.setTodo(resultSet.getString("todo"));
                schedule.setManager(resultSet.getString("manager"));
                schedule.setDate(resultSet.getString("date"));
                schedule.setTime(resultSet.getString("time"));
                return schedule;
            } else {
                return null;
            }

        }, id);
    }

}
