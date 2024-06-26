package com.sparta.scheduleradvanced.service;

import com.sparta.scheduleradvanced.dto.CommentRequestDto;
import com.sparta.scheduleradvanced.dto.CommentResponseDto;
import com.sparta.scheduleradvanced.entity.Comment;
import com.sparta.scheduleradvanced.entity.Schedule;
import com.sparta.scheduleradvanced.entity.User;
import com.sparta.scheduleradvanced.repository.CommentRepository;
import com.sparta.scheduleradvanced.repository.SchedulerRepository;
import com.sparta.scheduleradvanced.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {

    // 일정, 댓글 레포지토리 둘다 주입
    private final CommentRepository commentRepository;
    private final SchedulerRepository schedulerRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, SchedulerRepository schedulerRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.schedulerRepository = schedulerRepository;
        this.userRepository = userRepository;
    }

    // 해당 일정 댓글 조회
    public List<CommentResponseDto> getComments(Long id) {
        // 해당 일정의 모든 댓글 조회
        List<Comment> commentList = commentRepository.findByScheduleId(id);

        List<CommentResponseDto> responseDtoList = new ArrayList<>();
        for (Comment comment : commentList) {
            responseDtoList.add(new CommentResponseDto(comment));
        }
        return responseDtoList;
    }

    // 댓글 등록
    public CommentResponseDto createComment(Long id, CommentRequestDto requestDto, User user) {
        // 해당 일정 확인
        Schedule schedule = findSchedule(id);
        // Save
        Comment comment = commentRepository.save(new Comment(schedule, requestDto, findUser(user.getUsername())));
        return new CommentResponseDto(comment);
    }

    @Transactional
    // 댓글 수정
    public CommentResponseDto updateComment(Long scheduleId, Long commentId, CommentRequestDto requestDto, User user) {
        // 작성자와 일치 확인
        Comment comment = matchScheduleAndUser(scheduleId, commentId, findUser(user.getUsername()));
        // Update
        comment.update(requestDto);
        return new CommentResponseDto(comment);
    }


    // 댓글 삭제
    public String deleteComment(Long scheduleId, Long commentId, User user) {
        // 작성자와 일치 확인
        Comment comment = matchScheduleAndUser(scheduleId, commentId, findUser(user.getUsername()));
        // Delete
        commentRepository.delete(comment);
        return "댓글이 삭제되었습니다.";
    }

    /*일정 존재 여부 확인 메서드*/
    private Schedule findSchedule(Long scheduleId) {
        // 존재할 경우, schedule 반환, null 일 경우 예외
        return schedulerRepository.findById(scheduleId).orElseThrow(
                () -> new IllegalArgumentException("해당 일정은 존재하지 않습니다.")
        );
    }

    /*일정 존재 여부 확인 메서드*/
    private Comment findComment(Long commentId) {
        // 존재할 경우, schedule 반환, null 일 경우 예외
        return commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("선택한 댓글은 존재하지 않습니다.")
        );
    }

    /*일정 id, 댓글 id - User id 일치 확인메서드*/
    private Comment matchScheduleAndUser(Long scheduleId, Long commentId, User user) {

        // 해당 일정 확인
        this.findSchedule(scheduleId);

        // 해당 댓글 확인
        Comment comment = findComment(commentId);

        // 사용자가 작성한 댓글인지 확인
        if (!comment.getUser().getUsername().equals(user.getUsername())) {
            throw new IllegalArgumentException("작성자만 삭제/수정할 수 있습니다.");
        }
        return comment;
    }

    /*User DB에 있는 User 가지고오는 메서드*/
    private User findUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                ()-> new IllegalArgumentException("해당 사용자는 존재하지 않습니다.")
        );

        return user;
    }

}
