package com.sparta.scheduleradvanced.service;

import com.sparta.scheduleradvanced.dto.SchedulerRequestDto;
import com.sparta.scheduleradvanced.dto.SchedulerResponseDto;
import com.sparta.scheduleradvanced.entity.Schedule;
import com.sparta.scheduleradvanced.entity.User;
import com.sparta.scheduleradvanced.entity.UserRoleEnum;
import com.sparta.scheduleradvanced.repository.SchedulerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SchedulerService {

    private final SchedulerRepository schedulerRepository;

    public SchedulerService(SchedulerRepository schedulerRepository) {
        this.schedulerRepository = schedulerRepository;
    }


    // 일정 등록
    public SchedulerResponseDto createSchedule(SchedulerRequestDto requestDto, User user) {
        // RequestDto -> Entity Repository에서 저장
        Schedule schedule = schedulerRepository.save(new Schedule(requestDto, user));
        ;
        return new SchedulerResponseDto(schedule);
    }

    // USER의 일정 조회
    public List<SchedulerResponseDto> getSchedules() {
        // 모든 일정 조회
        List<Schedule> scheduleList = schedulerRepository.findAll();
        List<SchedulerResponseDto> responseDtoList = new ArrayList<>();

        for (Schedule schedule : scheduleList) {
            responseDtoList.add(new SchedulerResponseDto(schedule));
        }

        return responseDtoList;
    }

    @Transactional
    /*일정 수정*/
    public SchedulerResponseDto updateSchedule(Long id, SchedulerRequestDto requestDto, User user) {
        Schedule schedule = matchScheduleAndUser(id, user);
        // Update
        schedule.update(requestDto);
        return new SchedulerResponseDto(schedule);
    }

    // 일정 삭제
    public String deleteSchedule(Long schduleId, User user) {
        Schedule schedule = matchScheduleAndUser(schduleId, user);
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

        if (schedule.getUser().getId() != user.getId()) {
            throw new IllegalArgumentException("다른 사용자가 작성한 일정입니다.");
        }
        return schedule;
    }
}
