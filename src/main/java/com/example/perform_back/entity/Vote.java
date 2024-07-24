package com.example.perform_back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import lombok.Data;

@Entity
@Data
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="vote_id")
    private Long id;
    private Integer agreeNum = 0;
    private Integer disagreeNum = 0;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;

    @OneToOne(mappedBy = "vote")
    private ReviewPost reviewPost;

    public Vote() {

        this.createdDate = new Date();
        // dueDate를 현재 날짜로부터 일주일 후로 설정
        this.dueDate = new Date(System.currentTimeMillis() + 1L * 60 * 1000); //테스트를 위해 dueDate를 1분 뒤로 수정
        //this.dueDate = new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000);
    }
}
