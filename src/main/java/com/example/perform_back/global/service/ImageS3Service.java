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
import java.util.List;
import java.util.UUID;

@Service
public class ImageS3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName; //버킷 이름

    @Autowired
    public ImageS3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    private String changedImageName(String originName) { //이미지 이름 중복 방지를 위해 랜덤으로 생성
        String random = UUID.randomUUID().toString();
        return random+originName;
    }

    private String uploadImageToS3(MultipartFile image) { //이미지를 S3에 업로드하고 이미지의 url을 반환
        String originName = image.getOriginalFilename(); //원본 이미지 이름
        String ext = originName.substring(originName.lastIndexOf(".")); //확장자
        String changedName = changedImageName(originName); //새로 생성된 이미지 이름

        ObjectMetadata metadata = new ObjectMetadata(); // 메타데이터
        metadata.setContentLength(image.getSize());
        metadata.setContentType("image/"+ext);
        try {
            amazonS3.putObject(new PutObjectRequest(bucketName, changedName, image.getInputStream(), metadata));
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 오류");
        }
        return amazonS3.getUrl(bucketName, changedName).toString(); //데이터베이스에 저장할 이미지가 저장된 주소

    }

    public Attachment uploadImage(MultipartFile image){
        String originName = image.getOriginalFilename();
        String storedImagePath = uploadImageToS3(image);
        return new Attachment(originName, storedImagePath);
    }

    public void deleteImage(Attachment attachment) {
            String url = attachment.getPath();
            String splitStr = ".com/";
            String fileName = url.substring(url.lastIndexOf(splitStr) + splitStr.length());
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));
    }
}