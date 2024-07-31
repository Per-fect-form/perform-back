package com.example.perform_back.repository;

import com.example.perform_back.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    List<Likes> findByPost(Post post);
    List<Likes> findByComment(Comment comment);

}
