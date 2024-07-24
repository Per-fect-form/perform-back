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
        Vote vote = voteRepository.findById(voteId).orElseThrow(() -> new IllegalArgumentException("�ش� ���̵��� �ɻ�Խù��� �������� �ʽ��ϴ�"));
        vote.setAgreeNum(vote.getAgreeNum() + 1);
        voteRepository.save(vote);
    }

    @Transactional
    public void disagree(Long voteId) {
        Vote vote = voteRepository.findById(voteId).orElseThrow(() -> new IllegalArgumentException("�ش� ���̵��� �ɻ�Խù��� �������� �ʽ��ϴ�"));
        vote.setDisagreeNum(vote.getDisagreeNum() + 1);
        voteRepository.save(vote);
    }
    @Scheduled(cron = "0 0 * * * *") // �� �ð����� ����, �� �� �� �� �� ����
    @Transactional
    public void updateReviewStatuses() {
        Date now = new Date();
        List<Vote> votes = voteRepository.findAll(); //��ǥ ��ü�� ����Ʈ�� ��������

        for (Vote vote : votes) { //�ϳ��� �˻�
            if (vote.getDueDate().before(now)) { //���������� ��������
                ReviewPost reviewPost = vote.getReviewPost();
                //���� ���
                int totalVotes = vote.getAgreeNum() + vote.getDisagreeNum();
                if (totalVotes > 0 && ((double) vote.getAgreeNum() / totalVotes) >= 0.7) { //��ü ��ǥ ���� ���� �� ���� ���� ����
                    reviewPost.setReviewStatus("pass");
                } else {
                    reviewPost.setReviewStatus("non_pass");
                }
                reviewPostRepository.save(reviewPost);
            }
        }
    }
}
