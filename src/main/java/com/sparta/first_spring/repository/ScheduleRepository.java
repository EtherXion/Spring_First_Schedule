package com.sparta.first_spring.repository;

import com.sparta.first_spring.dto.ScheduleRequestDto;
import com.sparta.first_spring.dto.ScheduleResponseDto;
import com.sparta.first_spring.entity.Schedule;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class ScheduleRepository {

    public final JdbcTemplate jdbcTemplate;

    public ScheduleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public Schedule save(Schedule schedule) {
        // DB 저장
        KeyHolder keyHolder = new GeneratedKeyHolder(); // 기본 키 반환받기 위한 객체

        String sql = "INSERT INTO schedule_table (todo, manager, password, date, modify_date) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update( con ->  {
                    PreparedStatement ps = con.prepareStatement(sql,
                            Statement.RETURN_GENERATED_KEYS);

                    ps.setString(1, schedule.getTodo());
                    ps.setString(2, schedule.getManager());
                    ps.setString(3, schedule.getPassword());
                    // schedule 파일들 고쳤는데 여긴 안고쳐도 되나?
                    ps.setTimestamp(4, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
                    ps.setTimestamp(5, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
                    return ps;
                },
                keyHolder);

        // DB Insert 후 받아온 기본키 확인
        Long id = keyHolder.getKey().longValue();
        schedule.setId(id); // Math.toIntExact(id)

        // ScheduleResponseDto scheduleResponseDto = new ScheduleResponseDto(schedule);

        return schedule; // scheduleResponseDto

    }

    public List<ScheduleResponseDto> findAll() {
        // DB 조회

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

    public ScheduleResponseDto find(Long id){
        // DB 조회

        // 해당 일정이 존재하는지 id 찾아오는거
        Schedule schedule = findById(id);

        if (schedule != null) {
            String sql = "SELECT * FROM schedule_table WHERE id = ? ";

            return jdbcTemplate.queryForObject(sql,new RowMapper<ScheduleResponseDto>() {
                @Override
                public ScheduleResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                    // SQL 결과로 받아온 데이터 ScheduleResponseDto 타입으로 변환
                    Long id = rs.getLong("id");
                    String todo = rs.getString("todo");
                    String manager = rs.getString("manager");
                    Timestamp date = rs.getTimestamp("date");
                    Timestamp modify_date = rs.getTimestamp("modify_date");
                    return new ScheduleResponseDto(id, todo, manager, date, modify_date);
                }
            }, id);
        } else {
            throw new IllegalArgumentException("Schedule not found");
        }
    }

    public ScheduleResponseDto update(Long id , ScheduleRequestDto requestDto) {
        // DB 수정

        Schedule schedule = findById(id);

        if (schedule != null){

            // 비밀번호 일치 확인 부분 일치하지 않으면 오류 처리 비어있으면 오류 처리
            if (schedule.getPassword() == null || !schedule.getPassword().equals(requestDto.getPassword())) {
                throw new IllegalArgumentException("Passwords don't match");
            }

            // 일정 내용 수정
            String sql = "UPDATE schedule_table SET todo = ?, manager = ?, modify_date = ? WHERE id = ?";
            // requestDto.getModify_date() - > LocalDateTime.now() 변경 입력 안받고 바로 현재 시간으로
            jdbcTemplate.update(sql, requestDto.getTodo(), requestDto.getManager(), LocalDateTime.now(), id);

            // findById 한번 더 하면 되나?
            Schedule updateSchedule = findById(id);

            ScheduleResponseDto scheduleResponseDto = new ScheduleResponseDto(updateSchedule); // findById 새로 한거 받아옴

            return scheduleResponseDto;

        } else {
            throw new IllegalArgumentException("Schedule not found");
        }
    }

    public Long delete(Long id , ScheduleRequestDto requestDto) {
        // 삭제

        Schedule schedule = findById(id);
        if (schedule != null){

            // 비밀번호 일치 확인 부분 일치하지 않으면 오류 처리 비어있으면 오류 처리
            if (schedule.getPassword() == null || !schedule.getPassword().equals(requestDto.getPassword())) {
                throw new IllegalArgumentException("Passwords don't match");
            }

            String sql = "DELETE FROM schedule_table WHERE id = ?";
            jdbcTemplate.update(sql, id);

            return id;
        } else {
            throw new IllegalArgumentException("Schedule not found");
        }
    }

    public Schedule findById(Long id) {
        // Id 확인

        String sql = "SELECT * FROM schedule_table WHERE id = ?";

        return jdbcTemplate.query(sql, resultSet ->{
            if (resultSet.next()){
                Schedule schedule = new Schedule();

                schedule.setId(resultSet.getLong("id"));

                schedule.setTodo(resultSet.getString("todo"));
                schedule.setManager(resultSet.getString("manager"));

                schedule.setPassword(resultSet.getString("password")); // 비밀번호 일치 null 문제로 임시로 추가

                schedule.setDate(resultSet.getTimestamp("date"));
                schedule.setModify_date(resultSet.getTimestamp("modify_date"));
                return schedule;
            } else {
                return null;
            }

        }, id);
    }




}
