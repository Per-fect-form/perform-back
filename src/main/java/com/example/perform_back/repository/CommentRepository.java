package com.example.perform_back.repository;

import com.example.perform_back.entity.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>{

    void deleteAllByPostId(Long reviewPostId);
    List<Comment> findByPostId(Long reviewPostId);
}
