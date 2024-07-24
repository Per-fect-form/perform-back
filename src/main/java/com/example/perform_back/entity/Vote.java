package com.example.perform_back.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonBackReference
    private ReviewPost reviewPost;

    public Vote() {

        this.createdDate = new Date();

        this.dueDate = new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000);

        //this.dueDate = new Date(System.currentTimeMillis() + 1L * 60 * 1000); 
        this.dueDate = new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000);

    }
}
