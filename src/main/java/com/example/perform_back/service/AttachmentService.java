package com.example.perform_back.service;

import com.example.perform_back.dto.AttachmentDto;
import com.example.perform_back.entity.Attachment;
import com.example.perform_back.entity.Post;
import com.example.perform_back.entity.ReviewPost;
import com.example.perform_back.global.service.FileS3Service;
import com.example.perform_back.repository.AttachmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttachmentService {

    private AttachmentRepository attachmentRepository;
    private FileS3Service fileS3Service;

    public AttachmentService(AttachmentRepository attachmentRepository, FileS3Service fileS3Service) {
        this.attachmentRepository = attachmentRepository;
        this.fileS3Service = fileS3Service;
    }
    public Attachment save(MultipartFile file) {
        String ext = getFileExtension(file.getOriginalFilename());
        Attachment attachment;
        if (isImageFile(ext)) {
            attachment = fileS3Service.uploadImage(file);
        } else if (isVideoFile(ext)) {
            attachment = fileS3Service.uploadVideo(file);
        } else {
            throw new RuntimeException("Unsupported file type");
        }
        return attachmentRepository.save(attachment);
    }

    public void savePostWithAttachment(Post post, MultipartFile file) {
        Attachment attachment = save(file);
        attachment.setPost(post);
        attachmentRepository.save(attachment);
    }

    public void savePostWithAttachment(ReviewPost reviewPost, MultipartFile file) {
        Attachment attachment = save(file);
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

    @Transactional
    public void deleteAllByPost(Post post) {
        List<Attachment> attachments = attachmentRepository.findByPost(post);
        for (Attachment attachment : attachments){
            deleteById(attachment);
        }
    }
    @Transactional
    public void deleteAllByReviewPost(ReviewPost reviewPost) {
            List<Attachment> attachments = attachmentRepository.findByReviewPost(reviewPost);
            for (Attachment attachment : attachments) {
                deleteById(attachment);
            }
        }

    public void deleteById(Attachment attachment) {
        fileS3Service.deleteFile(attachment);
        attachmentRepository.deleteById(attachment.getId());
    }

}
