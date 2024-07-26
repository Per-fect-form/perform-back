package com.example.perform_back.repository;

import com.example.perform_back.entity.Attachment;
import com.example.perform_back.entity.Post;
import com.example.perform_back.entity.ReviewPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByPost(Post savedPost);
    List<Attachment> findByReviewPost(ReviewPost savedPost);


}
