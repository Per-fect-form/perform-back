package com.example.perform_back.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_id")
    private Long id;
    private String name;
    private String path;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonBackReference
    private Post post;

    @ManyToOne
    @JoinColumn(name = "review_post_id")
    @JsonBackReference
    private ReviewPost reviewPost;

    public Attachment(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public Attachment() {

    }
}
