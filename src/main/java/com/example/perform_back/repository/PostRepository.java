package com.example.perform_back.repository;

import com.example.perform_back.entity.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository  extends JpaRepository<Post, Long> {
    List<Post> findByTitleContaining(String title);
}
