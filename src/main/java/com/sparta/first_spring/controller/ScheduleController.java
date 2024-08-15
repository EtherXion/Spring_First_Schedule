package com.sparta.first_spring.controller;

import com.sparta.first_spring.dto.ScheduleRequestDto;
import com.sparta.first_spring.dto.ScheduleResponseDto;
import com.sparta.first_spring.entity.Schedule;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.time.LocalDateTime;
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

        String sql = "INSERT INTO schedule_table (todo, manager, password, date, modify_date) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update( con ->  {
                    PreparedStatement ps = con.prepareStatement(sql,
                            Statement.RETURN_GENERATED_KEYS);

                    ps.setString(1, requestDto.getTodo());
                    ps.setString(2, requestDto.getManager());
                    ps.setString(3, requestDto.getPassword());
                    // schedule 파일들 고쳤는데 여긴 안고쳐도 되나?
                    ps.setTimestamp(4, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now())); // setAccessDate ? LocalDateTime.now 원래 그냥 requestDto.date()
                    ps.setTimestamp(5, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now())); // 현재 시간?
                    return ps;
                },
                keyHolder);

        // DB Insert 후 받아온 기본키 확인
        Long id = keyHolder.getKey().longValue();
        schedule.setId(Math.toIntExact(id));

        ScheduleResponseDto scheduleResponseDto = new ScheduleResponseDto(schedule);

        return scheduleResponseDto;

        // 뭔가 반환도 잘 되는군 이제

    }

    @GetMapping("/schedule")
    public List<ScheduleResponseDto> getSchedules() {

        String sql = "SELECT * FROM schedule_table ORDER BY modify_date DESC";

        return jdbcTemplate.query(sql, new RowMapper<ScheduleResponseDto>() {
            @Override
            public ScheduleResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                // SQL 결과로 받아온 데이터 ScheduleResponseDto 타입으로 변환
                Long id = rs.getLong("id");
                String todo = rs.getString("todo");
                String manager = rs.getString("manager");
                Timestamp date = rs.getTimestamp("date"); // getTimestamp 종류가 여러개임
                Timestamp modify_date = rs.getTimestamp("modify_date");
                return new ScheduleResponseDto(id, todo, manager, date, modify_date);
            }
        });
    }

    @GetMapping("/schedule/{id}")
    public ScheduleResponseDto getSchedules_id(@PathVariable Long id) { // 인텔리제이 자동 처리로 변경되서 왜 된건지를 모름 지금
        // queryForObject 는 단일만 받아오니까 리스트가 아니라 단일 객체인 듯?
        // id 있는지 확인 하려면 받아야 하니까?

        // 해당 일정이 존재하는지
        Schedule schedule = findById(id);

        if (schedule != null) {
            // 아마 SQL 문에서 id를 기준으로 찾아야 할 듯?
            String sql = "SELECT * FROM schedule_table WHERE id = ? ";

            return jdbcTemplate.queryForObject(sql,new RowMapper<ScheduleResponseDto>() {
                @Override
                public ScheduleResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                    // SQL 결과로 받아온 데이터 ScheduleResponseDto 타입으로 변환
                    Long id = rs.getLong("id");
                    String todo = rs.getString("todo");
                    String manager = rs.getString("manager");
                    // 입력일은 직접 입력 받는게 아니라 자동으로 생성되야 하는거 아닌가?
                    Timestamp date = rs.getTimestamp("date"); // getTimestamp 종류가 여러개임
                    Timestamp modify_date = rs.getTimestamp("modify_date");
                    return new ScheduleResponseDto(id, todo, manager, date, modify_date);
                }
            }, id);
        } else {
            throw new IllegalArgumentException("Schedule not found");
        }
    }

    @PutMapping("/schedule/{id}")
    public ScheduleResponseDto updateSchedule(@PathVariable Long id, @RequestBody ScheduleRequestDto requestDto) {
        // 반환 받으려면 ScheduleResponseDto 필요한 듯?
        // 해당 일정이 존재하는지
        Schedule schedule = findById(id);
        if (schedule != null){
            // 일정 내용 수정
            String sql = "UPDATE schedule_table SET todo = ?, manager = ?, modify_date = ? WHERE id = ?";
            // requestDto.getModify_date() - > LocalDateTime.now() 변경 입력 안받고 바로 현재 시간으로 : 된 듯?
            jdbcTemplate.update(sql, requestDto.getTodo(), requestDto.getManager(), LocalDateTime.now(), id);

            ScheduleResponseDto scheduleResponseDto = new ScheduleResponseDto(schedule);

            return scheduleResponseDto; // 반환? 위랑 똑같게는 안될 듯? 뭐지 갑자기 되는것 같은데
            // 갑자기 id 가 0으로 반환되는데...
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
                schedule.setDate(resultSet.getTimestamp("date")); // 여기도 getTimestamp
                schedule.setModify_date(resultSet.getTimestamp("modify_date"));
                return schedule;
            } else {
                return null;
            }

        }, id);
    }

}
