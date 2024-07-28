package com.example.perform_back.global.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
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
public class FileS3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName; // 버킷 이름

    @Autowired
    public FileS3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    private String changedFileName(String originName) { // 파일 이름 중복 방지를 위해 랜덤으로 생성
        String random = UUID.randomUUID().toString();
        return random + originName;
    }

    private String uploadFileToS3(MultipartFile file, String type) { // 파일을 S3에 업로드하고 파일의 URL을 반환
        String originName = file.getOriginalFilename(); // 원본 파일 이름
        String ext = originName.substring(originName.lastIndexOf(".")); // 확장자
        String changedName = changedFileName(originName); // 새로 생성된 파일 이름

        ObjectMetadata metadata = new ObjectMetadata(); // 메타데이터
        metadata.setContentLength(file.getSize());
        metadata.setContentType(type + "/" + ext.substring(1));
        try {
            amazonS3.putObject(new PutObjectRequest(bucketName, changedName, file.getInputStream(), metadata));
        } catch (IOException e) {
            throw new RuntimeException(type + " 업로드 오류", e);
        }
        return amazonS3.getUrl(bucketName, changedName).toString(); // 데이터베이스에 저장할 파일이 저장된 주소
    }

    public Attachment uploadImage(MultipartFile image) {
        String originName = image.getOriginalFilename();
        String storedImagePath = uploadFileToS3(image, "image");
        return new Attachment(originName, storedImagePath);
    }

    public Attachment uploadVideo(MultipartFile video) {
        String originName = video.getOriginalFilename();
        String storedVideoPath = uploadFileToS3(video, "video");
        return new Attachment(originName, storedVideoPath);
    }

    public void deleteFile(Attachment attachment) {
        String url = attachment.getPath();
        String splitStr = ".com/";
        String fileName = url.substring(url.lastIndexOf(splitStr) + splitStr.length());
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));
    }
}
