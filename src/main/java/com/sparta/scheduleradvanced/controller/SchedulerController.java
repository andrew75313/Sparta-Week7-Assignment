package com.sparta.scheduleradvanced.controller;

import com.sparta.scheduleradvanced.dto.SchedulerRequestDto;
import com.sparta.scheduleradvanced.dto.SchedulerResponseDto;
import com.sparta.scheduleradvanced.security.UserDetailsImpl;
import com.sparta.scheduleradvanced.service.SchedulerService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SchedulerController {
    private final SchedulerService schedulerService;

    public SchedulerController(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    /*일정 등록*/
    @PostMapping("/schedules")
    public SchedulerResponseDto createScheduler(@RequestBody SchedulerRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return schedulerService.createSchedule(requestDto, userDetails.getUser());
    }

    /*일정 조회(USER)*/
    @GetMapping("/schedules")
    public List<SchedulerResponseDto> getSchedules() {
        return schedulerService.getSchedules();
    }

    /*일정 수정*/
    @PutMapping("/schedules/{id}")
    public SchedulerResponseDto updateSchedule(@PathVariable Long id, @RequestBody SchedulerRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return schedulerService.updateSchedule(id, requestDto, userDetails.getUser());
    }

    /*일정 삭제*/
    @DeleteMapping("/schedules/{id}")
    public String deleteSchedule(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return schedulerService.deleteSchedule(id, userDetails.getUser());
    }
}
