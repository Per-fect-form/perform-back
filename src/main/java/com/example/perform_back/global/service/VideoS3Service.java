package com.example.perform_back.global.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.perform_back.entity.Attachment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class VideoS3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName; // 버킷 이름

    @Autowired
    public VideoS3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    private String changedVideoName(String originName) { // 비디오 이름 중복 방지를 위해 랜덤으로 생성
        String random = UUID.randomUUID().toString();
        return random + originName;
    }

    private String uploadVideoToS3(MultipartFile video) { // 비디오를 S3에 업로드하고 비디오의 URL을 반환
        String originName = video.getOriginalFilename(); // 원본 비디오 이름
        String ext = originName.substring(originName.lastIndexOf(".")); // 확장자
        String changedName = changedVideoName(originName); // 새로 생성된 비디오 이름

        ObjectMetadata metadata = new ObjectMetadata(); // 메타데이터
        metadata.setContentLength(video.getSize());
        metadata.setContentType("video/" + ext);
        try {
            amazonS3.putObject(new PutObjectRequest(bucketName, changedName, video.getInputStream(), metadata));
        } catch (IOException e) {
            throw new RuntimeException("비디오 업로드 오류");
        }
        return amazonS3.getUrl(bucketName, changedName).toString(); // 데이터베이스에 저장할 비디오가 저장된 주소
    }

    public Attachment uploadVideo(MultipartFile video) {
        String originName = video.getOriginalFilename();
        String storedVideoPath = uploadVideoToS3(video);
        return new Attachment(originName, storedVideoPath);
    }
}

