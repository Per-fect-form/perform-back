package com.example.perform_back.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String username;
    private String profile;
    private Date createdDate;
    private boolean isExpert;
    private String snsUrl;
    private String email;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Post> posts;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Comment> comments;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Likes> likes;

    // 사용자가 수신한 알림 목록
    @OneToMany(mappedBy = "user")
    private List<Notification> receivedNotifications;

    // 사용자가 생성한 알림 목록
    @OneToMany(mappedBy = "actionUser")
    private List<Notification> createdNotifications;

}
