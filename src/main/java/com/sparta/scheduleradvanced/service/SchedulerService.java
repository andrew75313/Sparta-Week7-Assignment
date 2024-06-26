package com.sparta.scheduleradvanced.service;

import com.sparta.scheduleradvanced.dto.SchedulerRequestDto;
import com.sparta.scheduleradvanced.dto.SchedulerResponseDto;
import com.sparta.scheduleradvanced.entity.Schedule;
import com.sparta.scheduleradvanced.entity.User;
import com.sparta.scheduleradvanced.repository.SchedulerRepository;
import com.sparta.scheduleradvanced.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SchedulerService {

    private final SchedulerRepository schedulerRepository;
    private final UserRepository userRepository;

    public SchedulerService(SchedulerRepository schedulerRepository, UserRepository userRepository) {
        this.schedulerRepository = schedulerRepository;
        this.userRepository = userRepository;
    }

    // 모든 일정 조회
    public List<SchedulerResponseDto> getSchedules() {
        List<Schedule> scheduleList = schedulerRepository.findAllByOrderByCreatedAtDesc();
        List<SchedulerResponseDto> responseDtoList = new ArrayList<>();

        for (Schedule schedule : scheduleList) {
            responseDtoList.add(new SchedulerResponseDto(schedule));
        }

        return responseDtoList;
    }

    // 일정 등록
    public SchedulerResponseDto createSchedule(SchedulerRequestDto requestDto, User user) {
        // RequestDto -> Entity Repository에서 저장
        Schedule schedule = schedulerRepository.save(new Schedule(requestDto, findUser(user.getUsername())));
        return new SchedulerResponseDto(schedule);
    }

    @Transactional
    /*일정 수정*/
    public SchedulerResponseDto updateSchedule(Long id, SchedulerRequestDto requestDto, User user) {
        Schedule schedule = matchScheduleAndUser(id, findUser(user.getUsername()));
        // Update
        schedule.update(requestDto);
        return new SchedulerResponseDto(schedule);
    }

    // 일정 삭제
    public String deleteSchedule(Long schduleId, User user) {
        Schedule schedule = matchScheduleAndUser(schduleId, findUser(user.getUsername()));
        // Delete
        schedulerRepository.delete(schedule);

        return "일정이 삭제되었습니다.";
    }

    /*일정 존재 여부 확인 메서드*/
    private Schedule findSchedule(Long id) {
        // 존재할 경우, schedule 반환, null 일 경우 예외
        return schedulerRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("선택한 일정은 존재하지 않습니다.")
        );
    }

    /*일정 id - User 일치 확인메서드*/
    private Schedule matchScheduleAndUser(Long id, User user) {
        Schedule schedule = findSchedule(id);

        if (!schedule.getUser().getUsername().equals(user.getUsername())) {
            throw new IllegalArgumentException("작성자만 삭제/수정할 수 있습니다.");
        }
        return schedule;
    }

    /*User DB에 있는 User 가지고오는 메서드*/
    private User findUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                ()-> new IllegalArgumentException("해당 사용자는 존재하지 않습니다.")
        );

        return user;
    }

}
