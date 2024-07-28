package com.example.perform_back.service;

import com.example.perform_back.dto.ReviewPostDto;
import com.example.perform_back.entity.ReviewPost;
import com.example.perform_back.entity.Vote;
import com.example.perform_back.repository.ReviewPostRepository;
import com.example.perform_back.repository.VoteRepository;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ReviewPostService {

    private final ReviewPostRepository reviewPostRepository;
    private final VoteRepository voteRepository;
    private final AttachmentService attachmentService;

    @Autowired
    public ReviewPostService(ReviewPostRepository reviewPostRepository,
                             VoteRepository voteRepository,
                             AttachmentService attachmentService) {
        this.reviewPostRepository = reviewPostRepository;
        this.voteRepository = voteRepository;
        this.attachmentService = attachmentService;
    }
    public ReviewPost createReviewPost(ReviewPostDto reviewPostDto, MultipartFile[] files) throws Exception {

        Vote vote = new Vote();
        voteRepository.save(vote);

        ReviewPost reviewPostToSave = convertToPost(reviewPostDto, new ReviewPost());
        reviewPostToSave = reviewPostRepository.save(reviewPostToSave);
        reviewPostToSave.setVote(vote);
        if (files != null && files.length > 0) {
            saveMultipartFiles(files, reviewPostToSave);
        } else if (files == null) throw new IllegalArgumentException("No files");
        return reviewPostToSave;

    }
    public ReviewPost getReviewPostById(Long id) throws NoSuchElementException {
        Optional<ReviewPost> reviewPost = reviewPostRepository.findById(id);
        if (reviewPost.isEmpty()) throw new IllegalArgumentException("Post not found");
        return reviewPost.get();
    }

    public List<ReviewPost> getAllReviewPost() {
        return reviewPostRepository.findAll();
    }

    public void deleteReviewPostById(Long id) {
        Optional<ReviewPost> reviewPost = reviewPostRepository.findById(id);
        if (reviewPost.isEmpty()) throw new IllegalArgumentException("Post not found");
        attachmentService.deleteAllByReviewPost(reviewPost.get());
        reviewPostRepository.deleteById(id);
    }
    private void saveMultipartFiles(MultipartFile[] files, ReviewPost reviewPost) {
        for (MultipartFile file : files) {
            attachmentService.savePostWithAttachment(reviewPost, file);
        }
        reviewPost.setAttachments(attachmentService.findByReviewPost(reviewPost));
    }

    private static ReviewPost convertToPost(ReviewPostDto reviewPostDto, ReviewPost reviewPost) {
        reviewPost.setTitle(reviewPostDto.getTitle());
        reviewPost.setContent(reviewPostDto.getContent());
        reviewPost.setCreatedDate(new Date());
        return reviewPost;
    }
}
