package com.sparta.scheduleradvanced.repository;

import com.sparta.scheduleradvanced.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByScheduleId(Long id);
}
