package com.sparta.scheduleradvanced.entity;

import com.sparta.scheduleradvanced.dto.SchedulerRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "schedule") // 매핑할 테이블의 이름을 지정
@NoArgsConstructor
public class Schedule extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title", nullable = false, length = 500)
    private String title;
    @Column(name = "contents", nullable = false, length = 500)
    private String contents;
    // User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable= false)
    private User user;
    // Comment
    @OneToMany(mappedBy = "schedule", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Comment> commentList = new ArrayList<>();

    public Schedule(SchedulerRequestDto requestDto, User user) {
        this.title = requestDto.getTitle();
        this.contents = requestDto.getContents();
        this.user = user;
    }

    public void update(SchedulerRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.contents = requestDto.getContents();
    }
}
