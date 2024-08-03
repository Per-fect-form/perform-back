package com.example.perform_back.service;

import com.example.perform_back.entity.ReviewPost;
import com.example.perform_back.entity.User;
import com.example.perform_back.entity.UserVote;
import com.example.perform_back.entity.Vote;
import com.example.perform_back.repository.ReviewPostRepository;
import com.example.perform_back.repository.UserRepository;
import com.example.perform_back.repository.UserVoteRepository;
import com.example.perform_back.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import org.springframework.web.bind.annotation.RequestHeader;

@Service
public class UserVoteService {

    private final VoteRepository voteRepository;
    private final ReviewPostRepository reviewPostRepository;
    private final UserVoteRepository userVoteRepository;
    private final UserRepository userRepository;
    private final UserService userService;



    public UserVoteService(VoteRepository voteRepository, ReviewPostRepository reviewPostRepository,
        UserVoteRepository userVoteRepository, UserRepository userRepository,
        UserService userService) {
        this.voteRepository = voteRepository;
        this.reviewPostRepository = reviewPostRepository;
        this.userVoteRepository = userVoteRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Transactional
    public Vote vote(Long reviewPostId, boolean isAgree, String accessToken) {

        ReviewPost reviewPost = reviewPostRepository.findById(reviewPostId)
            .orElseThrow(() -> new IllegalArgumentException("Can't find review post by id"));
        Vote vote = reviewPost.getVote();

        User user = userService.findByAccessToken(accessToken);
        long userId = user.getId();

        if (userVoteRepository.existsByUserIdAndVoteReviewPostId(userId, reviewPostId)) {
            throw new RuntimeException("이미 투표를 했습니다");
        }

        UserVote userVote = createUserVote(user, vote, isAgree);
        userVoteRepository.save(userVote);

        updateVoteCounts(vote, isAgree);
        voteRepository.save(vote);

        return vote;
    }

    private UserVote createUserVote(User user, Vote vote, boolean isAgree) {
        UserVote userVote = new UserVote();
        userVote.setUser(user);
        userVote.setVote(vote);
        userVote.setIsAgree(isAgree);
        userVote.setVoteDate(new Date());
        return userVote;
    }

    private void updateVoteCounts(Vote vote, boolean isAgree) {
        if (isAgree) {
            vote.setAgreeNum(vote.getAgreeNum() + 1);
        } else {
            vote.setDisagreeNum(vote.getDisagreeNum() + 1);
        }
    }
}
