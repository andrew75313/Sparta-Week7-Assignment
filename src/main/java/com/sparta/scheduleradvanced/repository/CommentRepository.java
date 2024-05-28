package com.sparta.scheduleradvanced.repository;

import com.sparta.scheduleradvanced.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
