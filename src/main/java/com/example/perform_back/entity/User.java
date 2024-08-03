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
    private String snsUrl;
    private String email;
    private Date createdDate;
    private boolean isExpert;
    private boolean ad; //홍보의 기본 값은 on

    @OneToMany(mappedBy = "user")
    private List<Post> posts;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<ReviewPost> reviewPosts;

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
