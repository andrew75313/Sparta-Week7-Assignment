package com.sparta.scheduleradvanced.controller;

import com.sparta.scheduleradvanced.dto.CommentRequestDto;
import com.sparta.scheduleradvanced.dto.CommentResponseDto;
import com.sparta.scheduleradvanced.entity.User;
import com.sparta.scheduleradvanced.jwt.JwtUtil;
import com.sparta.scheduleradvanced.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {
    private final CommentService commentService;
    private final JwtUtil jwtUtil;

    public CommentController(CommentService commentService, JwtUtil jwtUtil) {
        this.commentService = commentService;
        this.jwtUtil = jwtUtil;
    }


    /*댓글 조회(USER)*/
    @GetMapping("/schedules/{id}/comments")
    public List<CommentResponseDto> getComments(@PathVariable Long id) {
        return commentService.getComments(id);
    }

    /*댓글 등록*/
    @PostMapping("/schedules/{id}/comments")
    public CommentResponseDto createComment(@PathVariable Long id,
                                            @Valid @RequestBody CommentRequestDto requestDto,
                                            HttpServletRequest request) {
        User user = jwtUtil.getUserFromHeader(request);
        return commentService.createComment(id, requestDto, user);
    }

    /*댓글 수정*/
    @PutMapping("/schedules/{scheduleId}/comments/{commentId}")
    public CommentResponseDto updateComment(@PathVariable Long scheduleId,
                                            @PathVariable Long commentId,
                                            @Valid @RequestBody CommentRequestDto requestDto,
                                            HttpServletRequest request) {
        User user = jwtUtil.getUserFromHeader(request);
        return commentService.updateComment(scheduleId, commentId, requestDto, user);
    }

    /*댓글 삭제*/
    @DeleteMapping("/schedules/{scheduleId}/comments/{commentId}")
    public String deleteComment(@PathVariable Long scheduleId,
                                @PathVariable Long commentId,
                                HttpServletRequest request) {
        User user = jwtUtil.getUserFromHeader(request);
        return commentService.deleteComment(scheduleId, commentId, user);
    }
}