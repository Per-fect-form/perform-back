package com.example.perform_back.repository;

import com.example.perform_back.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    List<Likes> findByPost(Post post);
    List<Likes> findByComment(Comment comment);
    List<Likes> findByReviewPost(ReviewPost reviewPost);

    void deleteByCommentId(Long id);
    void deleteAllByReviewPostId(Long id);
    void deleteAllByPostId(Long id);

    Likes findByUserAndPost(User user, Post post);
    Likes findByUserAndComment(User user, Comment comment);
    Likes findByUserAndReviewPost(User user, ReviewPost reviewPost);

    boolean existsByCommentAndUser(Comment comment, User user);
    boolean existsByPostAndUser(Post post, User user);
}
