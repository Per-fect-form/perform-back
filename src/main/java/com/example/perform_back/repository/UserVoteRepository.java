package com.example.perform_back.repository;

import com.example.perform_back.entity.UserVote;
import com.example.perform_back.entity.Vote;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserVoteRepository extends JpaRepository<UserVote, Long> {

    List<UserVote> findByVote(Vote vote);
    boolean existsByUserIdAndVoteReviewPostId(Long userId, Long reviewPostId);
}

