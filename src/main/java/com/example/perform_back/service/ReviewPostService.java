package com.example.perform_back.service;

import com.example.perform_back.entity.Attachment;
import com.example.perform_back.entity.ReviewPost;
import com.example.perform_back.entity.Vote;
import com.example.perform_back.global.service.ImageS3Service;
import com.example.perform_back.repository.ReviewPostRepository;
import com.example.perform_back.repository.VoteRepository;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ReviewPostService {

    private final ReviewPostRepository reviewPostRepository;
    private final VoteRepository voteRepository;
    private final ImageS3Service imageS3Service;
    private final AttachmentService attachmentService;

    @Autowired
    public ReviewPostService(ReviewPostRepository reviewPostRepository,
        VoteRepository voteRepository,
        ImageS3Service imageS3Service,
        AttachmentService attachmentService) {
        this.reviewPostRepository = reviewPostRepository;
        this.voteRepository = voteRepository;
        this.imageS3Service = imageS3Service;
        this.attachmentService = attachmentService;
    }
    public void createReviewPost(String title, String content, MultipartFile file) {

        Vote vote = new Vote();
        voteRepository.save(vote);

        ReviewPost reviewPost = new ReviewPost(title, content);
        reviewPost.setVote(vote);
        reviewPostRepository.save(reviewPost);

        Attachment attachment = imageS3Service.uploadImage(file);
        attachment.setReviewPost(reviewPost);
        attachmentService.save(attachment);
    }
    public ReviewPost getReviewPostById(Long id) throws NoSuchElementException {
        Optional<ReviewPost> rp = reviewPostRepository.findById(id);
        if (rp.isEmpty()) throw new IllegalArgumentException("게시물이 존재하지 않습니다");
        return rp.get();
    }
}
