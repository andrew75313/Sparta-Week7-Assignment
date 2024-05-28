package com.sparta.scheduleradvanced.service;

import com.sparta.scheduleradvanced.dto.CommentRequestDto;
import com.sparta.scheduleradvanced.dto.CommentResponseDto;
import com.sparta.scheduleradvanced.entity.Comment;
import com.sparta.scheduleradvanced.entity.Schedule;
import com.sparta.scheduleradvanced.entity.User;
import com.sparta.scheduleradvanced.repository.CommentRepository;
import com.sparta.scheduleradvanced.repository.SchedulerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        // 해당 일정 확인
        Schedule schedule = schedulerRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 일정은 존재하지 않습니다.")
        );

        Comment comment = commentRepository.save(new Comment(schedule, requestDto, user));
        return new CommentResponseDto(comment);
    }

    @Transactional
    // 댓글 수정
    public CommentResponseDto updateComment(Long scheduleId, Long commentId, CommentRequestDto requestDto, User user) {
        // 해당 스케쥴 확인
        Schedule schedule = schedulerRepository.findById(scheduleId).orElseThrow(
                () -> new IllegalArgumentException("해당 일정은 존재하지 않습니다.")
        );

        // 해당 댓글 확인
        Comment comment = commentRepository.findByScheduleIdAndId(schedule.getId(), commentId).orElseThrow(
                () -> new IllegalArgumentException("해당 일정에 댓글은 존재하지 않습니다.")
        );

        // 작성자와 일치 확인
        if (comment.getUser().getId() != user.getId()) {
            throw new IllegalArgumentException("다른 사용자가 작성한 댓글입니다.");
        }
        // Update
        comment.update(requestDto);
        return new CommentResponseDto(comment);
    }

    public String deleteCommnet(Long scheduleId, Long commentId, User user) {
        // 해당 스케쥴 확인
        Schedule schedule = schedulerRepository.findById(scheduleId).orElseThrow(
                () -> new IllegalArgumentException("해당 일정은 존재하지 않습니다.")
        );

        // 해당 댓글 확인
        Comment comment = commentRepository.findByScheduleIdAndId(schedule.getId(), commentId).orElseThrow(
                () -> new IllegalArgumentException("해당 일정에 댓글은 존재하지 않습니다.")
        );

        // 작성자와 일치 확인
        if (comment.getUser().getId() != user.getId()) {
            throw new IllegalArgumentException("다른 사용자가 작성한 댓글입니다.");
        }

        // Delete
        commentRepository.delete(comment);
        return "댓글이 삭제되었습니다.";
    }
}
