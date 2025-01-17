package com.example.perform_back.service;

import com.example.perform_back.dto.AttachmentDto;
import com.example.perform_back.dto.ReviewPostDto;
import com.example.perform_back.entity.*;
import com.example.perform_back.repository.LikesRepository;
import com.example.perform_back.repository.ReviewPostRepository;
import com.example.perform_back.repository.UserVoteRepository;
import com.example.perform_back.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class ReviewPostService {
    private final ReviewPostRepository reviewPostRepository;
    private final VoteRepository voteRepository;
    private final AttachmentService attachmentService;
    private final LikesRepository likesRepository;

    private final UserVoteRepository userVoteRepository;
    private final UserService userService;

    @Autowired
    public ReviewPostService(ReviewPostRepository reviewPostRepository,
                             VoteRepository voteRepository,
                             AttachmentService attachmentService, LikesRepository likesRepository,
                             UserVoteRepository userVoteRepository, UserService userService) {
        this.reviewPostRepository = reviewPostRepository;
        this.voteRepository = voteRepository;
        this.attachmentService = attachmentService;
        this.likesRepository = likesRepository;
        this.userVoteRepository = userVoteRepository;
        this.userService = userService;
    }
    public ReviewPostDto createReviewPost(ReviewPostDto reviewPostDto, MultipartFile[] files, String accessToken) {
        User user = userService.findByAccessToken(accessToken); // 저장하기 전에 accessToken 먼저 검사

        validateFiles(files); // 파일이 빈 경우에도 파일 유형문제로 처리되는 경우를 제어

        ReviewPost reviewPostToSave = convertToReviewPost(reviewPostDto, new ReviewPost());

        validateTitle(reviewPostToSave.getTitle()); //제목 없이 게시물 생성 시도

        Vote vote = new Vote();
        voteRepository.save(vote);

        reviewPostToSave = reviewPostRepository.save(reviewPostToSave);
        reviewPostToSave.setVote(vote);
        reviewPostToSave.setUser(user);
        saveMultipartFiles(files, reviewPostToSave);

        return convertToReviewPostDto(reviewPostToSave);
    }

    public ReviewPostDto getReviewPostById(Long id) throws NoSuchElementException {
        Optional<ReviewPost> reviewPost = reviewPostRepository.findById(id);
        if (reviewPost.isEmpty()) throw new IllegalArgumentException("Post not found");
        return convertToReviewPostDto(reviewPost.get());
    }

    public List<ReviewPostDto> getAllReviewPosts() {
        return convertToReviewPostDtoList(reviewPostRepository.findAll());
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

    private static ReviewPost convertToReviewPost(ReviewPostDto reviewPostDto, ReviewPost reviewPost) {
        reviewPost.setTitle(reviewPostDto.getTitle());
        reviewPost.setContent(reviewPostDto.getContent());
        reviewPost.setCreatedDate(new Date());
        return reviewPost;
    }

    public List<ReviewPostDto> getReviewPostByTitle(String title) {
        return convertToReviewPostDtoList(reviewPostRepository.findByTitleContaining(title));
    }

    public ReviewPostDto updateReviewPostById(Long id, ReviewPostDto reviewPostDto, MultipartFile[] files) {
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
        return convertToReviewPostDto(reviewPostRepository.save(reviewPostToUpdate));

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
            String fileExtension = getFileExtension(originalFilename);

            if (originalFilename == null || originalFilename.isEmpty() || !isSupportedExtension(fileExtension)) {
                throw new IllegalArgumentException("파일이 첨부되지 않았거나 지원되지 않는 파일 유형입니다. " + originalFilename);
            }
        }
    }
    private boolean isSupportedExtension(String extension) {
        String[] supportedExtensions = {"png", "jpg", "jpeg", "gif", "bmp", "webp", "mp4", "mpeg", "ogg", "webm", "mov"};
        for (String supportedExtension : supportedExtensions) {
            if (supportedExtension.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    public ReviewPost findById(Long reviewPostId) {
        Optional<ReviewPost> reviewPost = reviewPostRepository.findById(reviewPostId);
        if (reviewPost.isPresent())
            return reviewPost.get();
        else
            throw new RuntimeException("Post not found");
    }

    public List<ReviewPostDto> convertToReviewPostDtoList(List<ReviewPost> reviewPosts) {
        List<ReviewPostDto> reviewPostDtoList = new ArrayList<>();
        for (ReviewPost reviewPost : reviewPosts){
            reviewPostDtoList.add(convertToReviewPostDto(reviewPost));
        }
        return reviewPostDtoList;
    }

    public ReviewPostDto convertToReviewPostDto(ReviewPost reviewPost) {
        return ReviewPostDto.builder()
                .id(reviewPost.getId())
                .title(reviewPost.getTitle())
                .content(reviewPost.getContent())
                .createdDate(reviewPost.getCreatedDate())
                .reviewStatus(reviewPost.getReviewStatus())
                .voteId(reviewPost.getVote().getId())
                .username(reviewPost.getUser().getUsername())
                .userId(reviewPost.getUser().getId())
                .attachments(convertToAttchmentDtoList(reviewPost.getAttachments()))
                .build();
    }

    private List<AttachmentDto> convertToAttchmentDtoList(List<Attachment> attachments) {
        if(attachments == null)
            return null;

        else return attachments.stream()
                .map(attachment -> {
                    AttachmentDto dto = new AttachmentDto();
                    dto.setId(attachment.getId());
                    dto.setFilePath(attachment.getPath());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<ReviewPostDto> findMyReviewPosts(String accessToken) {
        User user = userService.findByAccessToken(accessToken);
        return convertToReviewPostDtoList(reviewPostRepository.findByUser(user));
    }
}
