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
    @Column(name = "user_id")
    private Long id;

    private String username;
    private String profile;
    private Date createdDate;
    private boolean isExpert;
    private String snsUrl;
    private String email;
    private boolean ad;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Post> posts;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<ReviewPost> reviewPosts;

    public void updateUserInfo(String username, String profile, String snsUrl, String email) {
        this.username = username;
        this.profile = profile;
        this.snsUrl = snsUrl;
        this.email = email;
        this.ad = true; //홍보의 기본 값은 on
    }

    // 이메일 제외
    public void updateUsername(String username) {
        this.username = username;
    }

    public void updateProfile(String profile) {
        this.profile = profile;
    }

    public void updateSnsUrl(String snsUrl) {
        this.snsUrl = snsUrl;
    }

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Comment> comments;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<UserVote> userVotes;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Likes> likes;
}
