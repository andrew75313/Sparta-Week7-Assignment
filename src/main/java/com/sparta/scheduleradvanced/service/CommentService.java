package com.sparta.scheduleradvanced.service;

import com.sparta.scheduleradvanced.dto.CommentRequestDto;
import com.sparta.scheduleradvanced.dto.CommentResponseDto;
import com.sparta.scheduleradvanced.entity.Comment;
import com.sparta.scheduleradvanced.entity.Schedule;
import com.sparta.scheduleradvanced.entity.User;
import com.sparta.scheduleradvanced.repository.CommentRepository;
import com.sparta.scheduleradvanced.repository.SchedulerRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {

    // 일정, 댓글 레포지토리 둘다 주입
    private final CommentRepository commentRepository;
    private final SchedulerRepository schedulerRepository;

    public CommentService(CommentRepository commentRepository, SchedulerRepository schedulerRepository) {
        this.commentRepository = commentRepository;
        this.schedulerRepository = schedulerRepository;
    }

    // 해당 일정 댓글 조회
    public List<CommentResponseDto> getComments(Long id) {

        List<Comment> commentList = commentRepository.findAll();
        List<CommentResponseDto> responseDtoList = new ArrayList<>();

        for (Comment comment : commentList) {
            responseDtoList.add(new CommentResponseDto(comment));
        }

        return responseDtoList;
    }

    // 댓글 등록
    public CommentResponseDto createComment(Long id, CommentRequestDto requestDto, User user) {
        // 해당 일정 찾기
        Schedule schedule = schedulerRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 일정은 존재하지 않습니다.")
        );

        Comment comment = commentRepository.save(new Comment(schedule, requestDto, user));
        return new CommentResponseDto(comment);
    }

}
