package com.example.perform_back.service;

import com.example.perform_back.entity.ReviewPost;
import com.example.perform_back.entity.Vote;
import com.example.perform_back.repository.ReviewPostRepository;
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


    public VoteService(VoteRepository voteRepository, ReviewPostRepository reviewPostRepository) {
        this.voteRepository = voteRepository;
        this.reviewPostRepository = reviewPostRepository;
    }

    @Transactional
    public void agree(Long voteId) {
        Vote vote = voteRepository.findById(voteId).orElseThrow(() -> new IllegalArgumentException("Invalid vote ID"));
        vote.setAgreeNum(vote.getAgreeNum() + 1);
        voteRepository.save(vote);
    }

    @Transactional
    public void disagree(Long voteId) {
        Vote vote = voteRepository.findById(voteId).orElseThrow(() -> new IllegalArgumentException("Invalid vote ID"));
        vote.setDisagreeNum(vote.getDisagreeNum() + 1);
        voteRepository.save(vote);
    }
    @Scheduled(cron = "0 0 * * * *") // 매 시간마다 실행, 초 분 시 일 월 요일
    @Transactional
    public void updateReviewStatuses() {
        Date now = new Date();
        List<Vote> votes = voteRepository.findAll(); //투표 전체를 리스트로 가져오기

        for (Vote vote : votes) { //하나씩 검사
            if (vote.getDueDate().before(now)) { //마감기한을 지났으면
                ReviewPost reviewPost = vote.getReviewPost();
                //비율 계산
                int totalVotes = vote.getAgreeNum() + vote.getDisagreeNum();
                if (totalVotes > 0 && ((double) vote.getAgreeNum() / totalVotes) >= 0.7) { //전체 투표 수는 유저 수 대비로 차후 수정
                    reviewPost.setReviewStatus("pass");
                } else {
                    reviewPost.setReviewStatus("non_pass");
                }
                reviewPostRepository.save(reviewPost);
            }
        }
    }
}
