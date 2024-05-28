package com.sparta.scheduleradvanced.repository;

import com.sparta.scheduleradvanced.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 일정 ID와 댓글 ID로 특정 댓글 찾기
    Optional<Comment> findByScheduleIdAndId(Long id, Long commentId);
}
