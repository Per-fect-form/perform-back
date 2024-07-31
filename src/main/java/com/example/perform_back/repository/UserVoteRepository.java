package com.example.perform_back.repository;

import com.example.perform_back.entity.UserVote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserVoteRepository extends JpaRepository<UserVote, Long> {
}

