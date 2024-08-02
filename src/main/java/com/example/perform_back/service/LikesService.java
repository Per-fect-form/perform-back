package com.example.perform_back.service;

import com.example.perform_back.dto.LikesDto;
import com.example.perform_back.dto.PostDto;
import com.example.perform_back.entity.Comment;
import com.example.perform_back.entity.Likes;
import com.example.perform_back.entity.Post;
import com.example.perform_back.entity.ReviewPost;
import com.example.perform_back.entity.User;
import com.example.perform_back.repository.LikesRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    private final UserService userService;

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

    public LikesDto likesPost(Long postId, String accessToken) throws JsonProcessingException {
        User user = userService.findByAccessToken(accessToken);
        Post post = postService.findById(postId);

        // 유저가 이미 해당 게시물에 공감을 눌렀는지 확인
        Likes existingLike = likesRepository.findByUserAndPost(user, post);

        // 이미 공감을 눌렀던 경우
        if (existingLike != null) {
            likesRepository.delete(existingLike);
            throw new RuntimeException("공감이 취소되었습니다.");
        } else {
            // 공감을 누르지 않은 경우
            Likes likes = new Likes();
            likes.setLikedDate(new Date());
            likes.setUser(user);
            likes.setPost(post);
            likesRepository.save(likes);
            return converToLikesDto(likes, "post");
        }
    }

    public LikesDto likesReviewPost(Long reviewPostId, String accessToken) throws JsonProcessingException {
        User user = userService.findByAccessToken(accessToken);
        ReviewPost reviewPost = reviewPostService.findById(reviewPostId);

        // 유저가 이미 해당 리뷰 게시물에 좋아요를 눌렀는지 확인
        Likes existingLike = likesRepository.findByUserAndReviewPost(user, reviewPost);

        // 이미 좋아요를 눌렀던 경우
        if (existingLike != null) {
            likesRepository.delete(existingLike);
            throw new RuntimeException("공감이 취소되었습니다.");
        } else {
            // 좋아요를 누르지 않은 경우
            Likes likes = new Likes();
            likes.setLikedDate(new Date());
            likes.setUser(user);
            likes.setReviewPost(reviewPost);
            likesRepository.save(likes);
            return converToLikesDto(likes, "reviewPost");
        }
    }

    public LikesDto likesComment(Long commentId, String accessToken) throws JsonProcessingException {
        User user = userService.findByAccessToken(accessToken);
        Comment comment = commentService.findById(commentId);

        // 유저가 이미 해당 댓글에 좋아요를 눌렀는지 확인
        Likes existingLike = likesRepository.findByUserAndComment(user, comment);

        // 이미 공감을 눌렀던 경우
        if (existingLike != null) {
            likesRepository.delete(existingLike);
            throw new RuntimeException("공감이 취소되었습니다.");
        } else {
            // 공감을 누르지 않은 경우
            Likes likes = new Likes();
            likes.setLikedDate(new Date());
            likes.setUser(user);
            likes.setComment(comment);
            likesRepository.save(likes);
            return converToLikesDto(likes, "comment");
        }
    }

    public void deleteById(Long id) {
        likesRepository.deleteById(id);
    }


    private LikesDto converToLikesDto(Likes likes, String type) {
        LikesDto.LikesDtoBuilder likesDtoBuilder = LikesDto.builder()
                .id(likes.getId())
                .userId(likes.getUser().getId())
                .date(new Date());
        return switch (type) {
            case "post" -> likesDtoBuilder.fromUserId(likes.getPost().getUser().getId()).build();
            case "comment" -> likesDtoBuilder.fromUserId(likes.getComment().getUser().getId()).build();
            case "reviewPost" -> likesDtoBuilder.fromUserId(likes.getReviewPost().getUser().getId()).build();
            default -> throw new RuntimeException("잘못된 타입입니다.");
        };
    }

}
