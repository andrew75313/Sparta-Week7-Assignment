package com.sparta.scheduleradvanced.controller;

import com.sparta.scheduleradvanced.dto.SchedulerRequestDto;
import com.sparta.scheduleradvanced.dto.SchedulerResponseDto;
import com.sparta.scheduleradvanced.entity.User;
import com.sparta.scheduleradvanced.jwt.JwtUtil;
import com.sparta.scheduleradvanced.service.SchedulerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SchedulerController {
    private final SchedulerService schedulerService;
    private final JwtUtil jwtUtil;

    public SchedulerController(SchedulerService schedulerService, JwtUtil jwtUtil) {
        this.schedulerService = schedulerService;
        this.jwtUtil = jwtUtil;
    }

    /*일정 등록*/
    @PostMapping("/schedules")
    public SchedulerResponseDto createScheduler(@RequestBody SchedulerRequestDto requestDto, HttpServletRequest request) {
        User user = jwtUtil.getUserFromHeader(request);
        return schedulerService.createSchedule(requestDto, user);
    }

    /*일정 조회(USER)*/
    @GetMapping("/schedules")
    public List<SchedulerResponseDto> getSchedules() {
        return schedulerService.getSchedules();
    }

    /*일정 수정*/
    @PutMapping("/schedules/{id}")
    public SchedulerResponseDto updateSchedule(@PathVariable Long id, @RequestBody SchedulerRequestDto requestDto, HttpServletRequest request) {
        User user = jwtUtil.getUserFromHeader(request);
        return schedulerService.updateSchedule(id, requestDto, user);
    }

    /*일정 삭제*/
    @DeleteMapping("/schedules/{id}")
    public String deleteSchedule(@PathVariable Long id, HttpServletRequest request) {
        User user = jwtUtil.getUserFromHeader(request);
        return schedulerService.deleteSchedule(id, user);
    }
}
