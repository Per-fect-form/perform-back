package com.example.perform_back.service;

import com.example.perform_back.entity.Attachment;
import com.example.perform_back.entity.Post;
import com.example.perform_back.entity.ReviewPost;
import com.example.perform_back.global.service.ImageS3Service;
import com.example.perform_back.global.service.VideoS3Service;
import com.example.perform_back.repository.AttachmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class AttachmentService {

    private AttachmentRepository attachmentRepository;
    private ImageS3Service imageS3Service;
    private VideoS3Service videoS3Service;

    public AttachmentService(AttachmentRepository attachmentRepository, ImageS3Service imageS3Service) {
        this.attachmentRepository = attachmentRepository;
        this.imageS3Service = imageS3Service;
    }
    public Attachment save(MultipartFile file) {
        String ext = getFileExtension(file.getOriginalFilename());
        Attachment attachment;
        if (isImageFile(ext)) {
            attachment = imageS3Service.uploadImage(file);
        } else if (isVideoFile(ext)) {
            attachment = videoS3Service.uploadVideo(file);
        } else {
            throw new RuntimeException("Unsupported file type");
        }
        return attachmentRepository.save(attachment);
    }
    public void savePostWithAttachment(Post post, MultipartFile file) {
        Attachment attachment = imageS3Service.uploadImage(file);
        attachment.setPost(post);
        attachmentRepository.save(attachment);
    }

    public void savePostWithAttachment(ReviewPost reviewPost, MultipartFile file) {
        Attachment attachment = imageS3Service.uploadImage(file);
        attachment.setReviewPost(reviewPost);
        attachmentRepository.save(attachment);
    }

    public Attachment findById(Long id) {
        Optional<Attachment> attachment = attachmentRepository.findById(id);
        if(attachment.isPresent()) {
            return attachment.get();
        } else {
            throw new RuntimeException("Attachment not found");
        }
    }

    public List<Attachment> findByPost(Post savedPost) {
        return attachmentRepository.findByPost(savedPost);
    }

    public List<Attachment> findByReviewPost(ReviewPost savedReviewPost) {
        return attachmentRepository.findByReviewPost(savedReviewPost);
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    private boolean isImageFile(String ext) {
        return ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || ext.equals("gif") || ext.equals("bmp") || ext.equals("tiff");
    }

    private boolean isVideoFile(String ext) {
        return ext.equals("mp4") || ext.equals("avi") || ext.equals("mov") || ext.equals("mkv") || ext.equals("wmv");
    }

    public void deleteAllByReviewPost(ReviewPost reviewPost) {
        List<Attachment> attachments = attachmentRepository.findByReviewPost(reviewPost);
        for (Attachment attachment : attachments) {
            deleteById(attachment);
        }
    }
    public void deleteById(Attachment attachment) {
        imageS3Service.deleteImage(attachment);
        attachmentRepository.deleteById(attachment.getId());
    }
}
