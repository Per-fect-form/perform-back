package com.example.perform_back.service;

import com.example.perform_back.dto.LikesDto;
import com.example.perform_back.entity.*;
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

    public LikesDto likes(String category, Long id, String accessToken) {
        User user = userService.findByAccessToken(accessToken);

        if(category.equals("post")) return likesPost(id, user);
        else if(category.equals("reviewPost")) return likesReviewPost(id, user);
        else if(category.equals("comment")) return likesComment(id, user);
        else throw new RuntimeException("올바르지 않은 카테고리입니다.");
    }

    public LikesDto likesPost(Long postId, User user) {
        Post post = postService.findById(postId);
        // 유저가 이미 해당 게시물에 공감을 눌렀는지 확인
        Likes existingLike = likesRepository.findByUserAndPost(user, post);

        // 이미 공감을 눌렀던 경우
        if (existingLike != null)
            throw new RuntimeException("이미 공감이 되어있습니다.");
        else {
            Likes likes = new Likes();
            likes.setLikedDate(new Date());
            likes.setUser(user);
            likes.setPost(post);
            likesRepository.save(likes);
            return converToLikesDto(likes, "post");
        }
    }

    public LikesDto likesReviewPost(Long reviewPostId, User user) {
        ReviewPost reviewPost = reviewPostService.findById(reviewPostId);
        // 유저가 이미 해당 리뷰 게시물에 좋아요를 눌렀는지 확인
        Likes existingLike = likesRepository.findByUserAndReviewPost(user, reviewPost);

        // 이미 좋아요를 눌렀던 경우
        if (existingLike != null)
            throw new RuntimeException("이미 공감이 되어있습니다.");
        else {
            Likes likes = new Likes();
            likes.setLikedDate(new Date());
            likes.setUser(user);
            likes.setReviewPost(reviewPost);
            likesRepository.save(likes);
            return converToLikesDto(likes, "reviewPost");
        }
    }

    public LikesDto likesComment(Long commentId, User user) {
        Comment comment = commentService.findById(commentId);
        // 유저가 이미 해당 댓글에 좋아요를 눌렀는지 확인
        Likes existingLike = likesRepository.findByUserAndComment(user, comment);

        // 이미 공감을 눌렀던 경우
        if (existingLike != null)
            throw new RuntimeException("이미 공감이 되어있습니다.");
        else {
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

    public void dislikes(String category, Long id, String accessToken) {
        User user = userService.findByAccessToken(accessToken);
        if(category.equals("post")) dislikesPost(id, user);
        else if(category.equals("reviewPost")) dislikesReviewPost(id, user);
        else if(category.equals("comment")) dislikesComment(id, user);
        else throw new RuntimeException("올바르지 않은 카테고리입니다.");
    }

    private void dislikesComment(Long commentId, User user) {
        Comment comment = commentService.findById(commentId);
        Likes existingLike = likesRepository.findByUserAndComment(user, comment);

        if (existingLike != null) likesRepository.delete(existingLike);
        else throw new RuntimeException("공감이 되어있지 않습니다.");
    }

    private void dislikesReviewPost(Long reviewPostId, User user) {
        ReviewPost reviewPost = reviewPostService.findById(reviewPostId);
        Likes existingLike = likesRepository.findByUserAndReviewPost(user, reviewPost);

        if (existingLike != null) likesRepository.delete(existingLike);
        else throw new RuntimeException("공감이 되어있지 않습니다.");
    }

    private void dislikesPost(Long postId, User user) {
        Post post = postService.findById(postId);
        Likes existingLike = likesRepository.findByUserAndPost(user, post);

        if (existingLike != null) likesRepository.delete(existingLike);
        else throw new RuntimeException("공감이 되어있지 않습니다.");
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
