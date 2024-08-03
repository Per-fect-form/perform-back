package com.example.perform_back.repository;

import com.example.perform_back.entity.Post;
import java.util.List;

import com.example.perform_back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository  extends JpaRepository<Post, Long> {
    List<Post> findByTitleContaining(String title);
    List<Post> findByUser(User user);
    List<Post> findByCategory(String category);
}
