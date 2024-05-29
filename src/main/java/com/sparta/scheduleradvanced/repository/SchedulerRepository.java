package com.sparta.scheduleradvanced.repository;

import com.sparta.scheduleradvanced.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchedulerRepository extends JpaRepository<Schedule, Long> {
    // 내림차순정렬
    List<Schedule> findAllByOrderByCreatedAtDesc();
}
