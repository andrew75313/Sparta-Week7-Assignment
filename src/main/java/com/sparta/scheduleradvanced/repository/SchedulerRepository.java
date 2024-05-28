package com.sparta.scheduleradvanced.repository;

import com.sparta.scheduleradvanced.entity.Schedule;
import com.sparta.scheduleradvanced.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchedulerRepository extends JpaRepository<Schedule, Long> {

    //User가 작성한 Schedule 등록일 순으로 내림차순 정렬
    List<Schedule> findAllByUserOrderByCreatedAtDesc(User user);

}
