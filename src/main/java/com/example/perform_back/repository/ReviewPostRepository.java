package com.example.perform_back.repository;

import com.example.perform_back.entity.ReviewPost;
import java.util.List;

import com.example.perform_back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewPostRepository extends JpaRepository<ReviewPost, Long> {

    List<ReviewPost> findByTitleContaining(String title);
    List<ReviewPost> findByUser(User user);
}
