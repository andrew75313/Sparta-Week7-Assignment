package com.sparta.scheduleradvanced.controller;

import com.sparta.scheduleradvanced.dto.CommentRequestDto;
import com.sparta.scheduleradvanced.dto.CommentResponseDto;
import com.sparta.scheduleradvanced.dto.SchedulerRequestDto;
import com.sparta.scheduleradvanced.dto.SchedulerResponseDto;
import com.sparta.scheduleradvanced.security.UserDetailsImpl;
import com.sparta.scheduleradvanced.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
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
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return commentService.createComment(id, requestDto, userDetails.getUser());
    }

    /*댓글 수정*/
    @PutMapping("/schedules/{scheduleId}/comments/{commentId}")
    public CommentResponseDto updateComment(@PathVariable Long scheduleId,
                                            @PathVariable Long commentId,
                                            @Valid @RequestBody CommentRequestDto requestDto,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return commentService.updateComment(scheduleId, commentId, requestDto, userDetails.getUser());
    }
}