package com.example.perform_back.service;

import com.example.perform_back.entity.Comment;
import com.example.perform_back.entity.Likes;
import com.example.perform_back.entity.Post;
import com.example.perform_back.entity.ReviewPost;
import com.example.perform_back.entity.User;
import com.example.perform_back.repository.LikesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikesService {

    private final LikesRepository likesRepository;
    private final PostService postService;
    private final ReviewPostService reviewPostService;
    private final CommentService commentService;

    public List<Likes> findPostLikes(Long postId) {
        Post post = postService.findById(postId);
        return likesRepository.findByPost(post);
    }

    public List<Likes> findReviewPostLikes(Long reviewPostId) {
        ReviewPost reviewPost = reviewPostService.findById(reviewPostId);
        return likesRepository.findByReviewPost(reviewPost);
    }

    public List<Likes> findCommentLikes(Long commentId) {
        Comment comment = commentService.findById(commentId);
        return likesRepository.findByComment(comment);
    }

    public Likes likesPost(Long postId, String username) {
        Post post = postService.findById(postId);
        Likes likes = new Likes();
        likes.setLikedDate(new Date());
//        likes.setUser(new User(username));
        likes.setPost(post);
        likesRepository.save(likes);
        return likes;
    }
    public Likes likesReviewPost(Long reviewPostId, String username) {
        ReviewPost reviewPost = reviewPostService.findById(reviewPostId);
        Likes likes = new Likes();
        likes.setLikedDate(new Date());
//        likes.setUser(new User(username));
        likes.setReviewPost(reviewPost);
        likesRepository.save(likes);
        return likes;
    }

    public Likes likesComment(Long commentId, String username) {
        Comment comment = commentService.findById(commentId);
        Likes likes = new Likes();
        likes.setLikedDate(new Date());
//        likes.setUser(new User(username));
        likes.setComment(comment);
        likesRepository.save(likes);
        return likes;
    }

    public void deleteById(Long id) {
        likesRepository.deleteById(id);
    }



}
