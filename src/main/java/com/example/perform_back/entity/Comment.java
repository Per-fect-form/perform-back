package com.example.perform_back.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Entity
@Data //lomboc 합친, 느낌 객체도 들어있음 ,,
public class Comment {
    // = 포스트 객체 그대로, 포스트 아이디로 해당 게시물 찾고, setpost 해서 comment post 연결
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;
    private String content;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Seoul")
    private Date createdDate;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonBackReference
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "comment")
    @JsonManagedReference
    private List<Likes> likes;

    public Comment() {}

    public void setPost(Post post) {
        this.post = post;
        if (!post.getComments().contains(this)){
            post.getComments().add(this);
        }
    }
}