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
                // 혹시 여기서 수정일 , 담당자명 두개만 보여줘야 하는건가..? 조건으로..?
                // 일정 목록 전부 보여주는 거니 특정 몇개만 보여주는건 아닐거고..?
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

        // 해당 일정이 존재하는지 id 찾아오는거
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
        // 비밀번호가 동일해야 수정 가능한? 느낌인 거겠지 이해 잘못한게 아니면...

        Schedule schedule = findById(id);

        // 조건문 여러개 쓰는게 맞나? 너무 어지럽게 만든 것 같은데
        if (schedule != null){

            // 비밀번호 일치 확인 부분 일치하지 않으면 오류 처리 비어있으면 오류 처리 근데 왜 비어있다 뜨는거임
            if (schedule.getPassword() == null || !schedule.getPassword().equals(requestDto.getPassword())) {
                throw new IllegalArgumentException("Passwords don't match");
            } // 뭔가 비밀번호가 null 어쩌고 하는데... DB에 저장은 되고 있음

            // 일정 내용 수정
            String sql = "UPDATE schedule_table SET todo = ?, manager = ?, modify_date = ? WHERE id = ?";
            // requestDto.getModify_date() - > LocalDateTime.now() 변경 입력 안받고 바로 현재 시간으로 : 된 듯?
            jdbcTemplate.update(sql, requestDto.getTodo(), requestDto.getManager(), LocalDateTime.now(), id);

            ScheduleResponseDto scheduleResponseDto = new ScheduleResponseDto(schedule);

            return scheduleResponseDto; // 반환? 위랑 똑같게는 안될 듯? 뭐지 갑자기 되는것 같은데
            // 갑자기 id 가 0으로 반환되는데... 아니 뭔가 수정 시간 부분도 좀 이상한 것 같은데
            // 반환되는 스캐줄이 수정되기 전 스캐줄임...

        } else {
            throw new IllegalArgumentException("Schedule not found");
        }
    }

    @DeleteMapping("/schedule/{id}")
    public Long deleteSchedule(@PathVariable Long id, @RequestBody ScheduleRequestDto requestDto) {
        // @RequestBody HTTP 요청의 본문 내용을 자바 객체로 변환
        // 비밀번호 일치 관련으로 들고오긴 했는데 맞나?

        Schedule schedule = findById(id);
        if (schedule != null){

            // 비밀번호 일치 확인 부분 일치하지 않으면 오류 처리 비어있으면 오류 처리 근데 왜 비어있다 뜨는거임
            if (schedule.getPassword() == null || !schedule.getPassword().equals(requestDto.getPassword())) {
                throw new IllegalArgumentException("Passwords don't match");
            } // 뭔가 비밀번호가 null 어쩌고 하는데... DB에 저장은 되고 있음
            // findById 에 password 가 없어서 난 문제였음

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

                // 여기서 id 도 set 해주면 수정 반환 부분에서 id까지 반환되려나..?
                schedule.setId(resultSet.getLong("id")); // 넣으니까 반환 됨

                schedule.setTodo(resultSet.getString("todo"));
                schedule.setManager(resultSet.getString("manager"));

                // 여가에 비밀번호 설정 안해서 안되던 거였음... 오류로 안 알려줘서 찾는데 오래 걸림...
                schedule.setPassword(resultSet.getString("password")); // 비밀번호 일치 null 문제로 임시로 추가

                schedule.setDate(resultSet.getTimestamp("date")); // 여기도 getTimestamp
                schedule.setModify_date(resultSet.getTimestamp("modify_date"));
                return schedule;
            } else {
                return null;
            }

        }, id);
    }

}
