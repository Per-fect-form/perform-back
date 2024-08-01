package com.example.perform_back.service;

import com.example.perform_back.dto.ReviewPostDto;
import com.example.perform_back.entity.ReviewPost;
import com.example.perform_back.entity.User;
import com.example.perform_back.entity.UserVote;
import com.example.perform_back.entity.Vote;
import com.example.perform_back.repository.LikesRepository;
import com.example.perform_back.repository.ReviewPostRepository;
import com.example.perform_back.repository.UserRepository;
import com.example.perform_back.repository.UserVoteRepository;
import com.example.perform_back.repository.VoteRepository;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
public class ReviewPostService {
    private final ReviewPostRepository reviewPostRepository;
    private final VoteRepository voteRepository;
    private final AttachmentService attachmentService;
    private final LikesRepository likesRepository;

    private final UserVoteRepository userVoteRepository;
    private final UserRepository userRepository;

    @Autowired
    public ReviewPostService(ReviewPostRepository reviewPostRepository,
                             VoteRepository voteRepository,
                             AttachmentService attachmentService, LikesRepository likesRepository,
        UserVoteRepository userVoteRepository, UserRepository userRepository) {
        this.reviewPostRepository = reviewPostRepository;
        this.voteRepository = voteRepository;
        this.attachmentService = attachmentService;
        this.likesRepository = likesRepository;
        this.userVoteRepository = userVoteRepository;
        this.userRepository = userRepository;
    }
    public ReviewPost createReviewPost(ReviewPostDto reviewPostDto, MultipartFile[] files) {

        validateFiles(files); // 파일이 빈 경우에도 파일 유형문제로 처리되는 경우를 제어

        //User user = userRepository.findById(userId).get();

        Vote vote = new Vote();
        voteRepository.save(vote);

        ReviewPost reviewPostToSave = convertToPost(reviewPostDto, new ReviewPost());

        validateTitle(reviewPostToSave.getTitle()); //제목 없이 게시물 생성 시도

        reviewPostToSave = reviewPostRepository.save(reviewPostToSave);
        reviewPostToSave.setVote(vote);
        //reviewPostToSave.setUser(user);

        saveMultipartFiles(files, reviewPostToSave);

        return reviewPostToSave;
    }
    public ReviewPost getReviewPostById(Long id) throws NoSuchElementException {
        Optional<ReviewPost> reviewPost = reviewPostRepository.findById(id);
        if (reviewPost.isEmpty()) throw new IllegalArgumentException("Post not found");
        return reviewPost.get();
    }

    public List<ReviewPost> getAllReviewPosts() {
        return reviewPostRepository.findAll();
    }

    @Transactional
    public void deleteReviewPostById(Long id) {
        ReviewPost reviewPost = reviewPostRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("ReviewPost not found"));

        Vote vote = reviewPost.getVote();
        List<UserVote> userVotes = userVoteRepository.findByVote(vote);

        userVoteRepository.deleteAll(userVotes); //관련된 유저투표 삭제
        attachmentService.deleteAllByReviewPost(reviewPost); //관련된 파일들 삭제
        likesRepository.deleteAllByReviewPostId(id);
        reviewPostRepository.deleteById(id); //심사 게시물 삭제
        voteRepository.delete(vote); //관련된 투표 삭제
    }
    private void saveMultipartFiles(MultipartFile[] files, ReviewPost reviewPost) {
        for (MultipartFile file : files) {
            attachmentService.savePostWithAttachment(reviewPost, file);
        }
        reviewPost.setAttachments(attachmentService.findByReviewPost(reviewPost));
    }

    private static ReviewPost convertToPost(ReviewPostDto reviewPostDto, ReviewPost reviewPost) {
        reviewPost.setTitle(reviewPostDto.getTitle());
        reviewPost.setContent(reviewPostDto.getContent());
        reviewPost.setCreatedDate(new Date());
        return reviewPost;
    }

    public List<ReviewPost> getReviewPostByTitle(String title) {
        return reviewPostRepository.findByTitleContaining(title);
    }

    public ReviewPost updateReviewPostById(Long id, ReviewPostDto reviewPostDto, MultipartFile[] files) {
        ReviewPost reviewPostToUpdate = reviewPostRepository.findById(id).get();
        if ((reviewPostToUpdate.getVote().getAgreeNum() + reviewPostToUpdate.getVote().getAgreeNum()) > 0) {
            throw new RuntimeException("이미 투표가 시작되어 게시물을 수정할 수 없습니다."); //투표가 시작된 후 수정 시도
        }
        validateTitle(reviewPostDto.getTitle()); //제목 없이 게시물 수정 시도

        validateFiles(files); // 파일이 빈 경우에도 파일 유형문제로 처리되는 경우를 제어

        reviewPostToUpdate.setContent(reviewPostDto.getContent());
        reviewPostToUpdate.setCreatedDate(new Date());

        attachmentService.deleteAllByReviewPost(reviewPostToUpdate);

        if (files != null && files.length > 0) {
            saveMultipartFiles(files, reviewPostToUpdate);
        }
        return reviewPostRepository.save(reviewPostToUpdate);

    }
    private boolean isSupportedContentType(String contentType) {
        if (contentType == null) {
            return false;
        }
        return contentType.startsWith("image/") || contentType.startsWith("video/");
    }
    private void validateTitle(String title) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("제목을 설정해주세요");
        }
    }

    private void validateFiles(MultipartFile[] files) {
        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            String mimeType = getMimeTypeFromExtension(originalFilename);

            if (!isSupportedContentType(mimeType)) {
                throw new IllegalArgumentException("파일이 첨부되지 않았거나 지원되지 않는 파일 유형입니다. " + originalFilename);
            }
        }
    }
    private static final Map<String, String> MIME_TYPES = new HashMap<>();
    static {
        MIME_TYPES.put("png", "image/png");
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("jpeg", "image/jpeg");
        MIME_TYPES.put("gif", "image/gif");
        MIME_TYPES.put("bmp", "image/bmp");
        MIME_TYPES.put("webp", "image/webp");
        MIME_TYPES.put("mp4", "video/mp4");
        MIME_TYPES.put("mpeg", "video/mpeg");
        MIME_TYPES.put("ogg", "video/ogg");
        MIME_TYPES.put("webm", "video/webm");
        MIME_TYPES.put("mov", "video/quicktime");
    }
    private String getMimeTypeFromExtension(String fileName) {
        String extension = getFileExtension(fileName);
        return MIME_TYPES.get(extension.toLowerCase());
    }
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}
