package com.example.perform_back.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ReviewPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "vote_id")
    private Vote vote;
    private String reviewStatus; //심사 중, 합격, 불합격
    private String title;
    private String content;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;


    public ReviewPost(String title, String content) {
        this.title = title;
        this.content = content;
        this.reviewStatus = "under review";
        this.createdDate = new Date();
    }
}
