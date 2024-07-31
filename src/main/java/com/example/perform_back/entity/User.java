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




    public void updateUserInfo(String username, String profile, String snsUrl, String email) {
        this.username = username;
        this.profile = profile;
        this.snsUrl = snsUrl;
    }

    //이메일 제외
    public void updateUsername(String username) {
        this.username = username;
    }

    public void updateProfile(String profile) {
        this.profile = profile;
    }

    public void updateSnsUrl(String snsUrl) {
        this.snsUrl = snsUrl;
    }

}
