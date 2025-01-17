package com.example.perform_back.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;
    private String title;
    private String content;
    private String category;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Seoul")
    private Date createdDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post")
    @JsonManagedReference
    private List<Attachment> attachments;

    @OneToMany(mappedBy = "post")
    @JsonManagedReference
    private List<Comment> comments;

    @OneToMany(mappedBy = "post")
    @JsonManagedReference
    private List<Likes> likes;

    public Post() {}

}
