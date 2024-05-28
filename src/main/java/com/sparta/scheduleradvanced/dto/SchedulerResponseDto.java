package com.sparta.scheduleradvanced.dto;

import com.sparta.scheduleradvanced.entity.Schedule;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SchedulerResponseDto {
    private Long id;
    private String title;
    private String contents;
    private String username;
    private LocalDateTime createdAt;

    public SchedulerResponseDto(Schedule scheduler) {
        this.id = scheduler.getId();
        this.title = scheduler.getTitle();
        this.contents = scheduler.getContents();
        this.username = scheduler.getUser().getUsername();
        this.createdAt = scheduler.getCreatedAt();
    }
}
