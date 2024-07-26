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
    private String bucketName; // ��Ŷ �̸�

    @Autowired
    public VideoS3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    private String changedVideoName(String originName) { // ���� �̸� �ߺ� ������ ���� �������� ����
        String random = UUID.randomUUID().toString();
        return random + originName;
    }

    private String uploadVideoToS3(MultipartFile video) { // ������ S3�� ���ε��ϰ� ������ URL�� ��ȯ
        String originName = video.getOriginalFilename(); // ���� ���� �̸�
        String ext = originName.substring(originName.lastIndexOf(".")); // Ȯ����
        String changedName = changedVideoName(originName); // ���� ������ ���� �̸�

        ObjectMetadata metadata = new ObjectMetadata(); // ��Ÿ������
        metadata.setContentLength(video.getSize());
        metadata.setContentType("video/" + ext);
        try {
            amazonS3.putObject(new PutObjectRequest(bucketName, changedName, video.getInputStream(), metadata));
        } catch (IOException e) {
            throw new RuntimeException("���� ���ε� ����");
        }
        return amazonS3.getUrl(bucketName, changedName).toString(); // �����ͺ��̽��� ������ ������ ����� �ּ�
    }

    public Attachment uploadVideo(MultipartFile video) {
        String originName = video.getOriginalFilename();
        String storedVideoPath = uploadVideoToS3(video);
        return new Attachment(originName, storedVideoPath);
    }
}

