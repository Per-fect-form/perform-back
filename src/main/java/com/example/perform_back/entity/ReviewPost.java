package com.example.perform_back.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review_post")
@Data
@NoArgsConstructor
public class ReviewPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_post_id")
    private Long id;
    @OneToOne
    @JoinColumn(name = "vote_id")
    @JsonManagedReference
    private Vote vote;
    private String reviewStatus; //�ɻ� ��, �հ�, ���հ�
    private String title;
    private String content;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @OneToMany(mappedBy = "reviewPost")
    @JsonManagedReference
    private List<Attachment> attachments;


    public ReviewPost(String title, String content) {
        this.title = title;
        this.content = content;
        this.reviewStatus = "under review";
        this.createdDate = new Date();
    }
}
