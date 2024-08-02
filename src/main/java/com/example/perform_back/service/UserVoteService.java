package com.example.perform_back.service;

import com.example.perform_back.entity.ReviewPost;
import com.example.perform_back.entity.UserVote;
import com.example.perform_back.entity.Vote;
import com.example.perform_back.repository.ReviewPostRepository;
import com.example.perform_back.repository.UserRepository;
import com.example.perform_back.repository.UserVoteRepository;
import com.example.perform_back.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class UserVoteService {

    private final VoteRepository voteRepository;
    private final ReviewPostRepository reviewPostRepository;
    private final UserVoteRepository userVoteRepository;
    private final UserRepository userRepository;


    public UserVoteService(VoteRepository voteRepository, ReviewPostRepository reviewPostRepository,
        UserVoteRepository userVoteRepository, UserRepository userRepository) {
        this.voteRepository = voteRepository;
        this.reviewPostRepository = reviewPostRepository;
        this.userVoteRepository = userVoteRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Vote vote(Long reviewPostId, String isAgree) {

        ReviewPost reviewPost = reviewPostRepository.findById(reviewPostId)
            .orElseThrow(() -> new IllegalArgumentException("Can't find review post by id"));
        Vote vote = reviewPost.getVote();

//        User user = userRepository.findById(userId)
//            .orElseThrow(() -> new IllegalArgumentException("Can't find user by id"));

        UserVote userVote = new UserVote();
//        if (userVoteRepository.existsByUserIdAndVoteReviewPostId(userId, reviewPostId)) {
//            throw new RuntimeException("이미 투표를 했습니다");
//        }
        //userVote.setUser(user);
        userVote.setVote(vote);
        userVote.setIsAgree(isAgree);
        userVote.setVoteDate(new Date());
        userVoteRepository.save(userVote);

        if ("agree".equalsIgnoreCase(isAgree)) {
            vote.setAgreeNum(vote.getAgreeNum() + 1);
        } else if ("disagree".equalsIgnoreCase(isAgree)) {
            vote.setDisagreeNum(vote.getDisagreeNum() + 1);
        }
        voteRepository.save(vote);
        return vote;
    }
}
