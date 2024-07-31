package com.example.perform_back.service;

import com.example.perform_back.entity.ReviewPost;
import com.example.perform_back.entity.User;
import com.example.perform_back.entity.UserVote;
import com.example.perform_back.entity.Vote;
import com.example.perform_back.repository.ReviewPostRepository;
import com.example.perform_back.repository.UserRepository;
import com.example.perform_back.repository.UserVoteRepository;
import com.example.perform_back.repository.VoteRepository;
import java.util.Date;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final ReviewPostRepository reviewPostRepository;
    private final UserVoteRepository userVoteRepository;
    private final UserRepository userRepository;

    public VoteService(VoteRepository voteRepository, ReviewPostRepository reviewPostRepository,
        UserVoteRepository userVoteRepository, UserRepository userRepository) {
        this.voteRepository = voteRepository;
        this.reviewPostRepository = reviewPostRepository;
        this.userVoteRepository = userVoteRepository;
        this.userRepository = userRepository;
    }


    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void updateReviewStatuses() {
        Date now = new Date();
        List<Vote> votes = voteRepository.findAll();

        for (Vote vote : votes) {
            if (vote.getDueDate().before(now)) {
                ReviewPost reviewPost = vote.getReviewPost();
                User user = reviewPost.getUser();

                int totalVotes = vote.getAgreeNum() + vote.getDisagreeNum();
                if (totalVotes > 0 && ((double) vote.getAgreeNum() / totalVotes) >= 0.7) {
                    reviewPost.setReviewStatus("pass");
                    updateUserExpertStatus(user, true);
                } else {
                    reviewPost.setReviewStatus("non_pass");
                }
                reviewPostRepository.save(reviewPost);
            }
        }
    }
    private void updateUserExpertStatus(User user, boolean isExpert) {
        user.setExpert(isExpert);
        userRepository.save(user);
    }
}
