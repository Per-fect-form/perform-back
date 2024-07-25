package com.example.perform_back.service;

import com.example.perform_back.entity.Attachment;
import com.example.perform_back.entity.Post;
import com.example.perform_back.global.service.ImageS3Service;
import com.example.perform_back.repository.AttachmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class AttachmentService {

    private AttachmentRepository attachmentRepository;
    private ImageS3Service imageS3Service;

    public AttachmentService(AttachmentRepository attachmentRepository, ImageS3Service imageS3Service) {
        this.attachmentRepository = attachmentRepository;
        this.imageS3Service = imageS3Service;
    }
    public Attachment save(MultipartFile file) {
        Attachment attachment = imageS3Service.uploadImage(file);
        return attachmentRepository.save(attachment);
    }
    public void savePostWithAttachment(Post post, MultipartFile file) {
        Attachment attachment = imageS3Service.uploadImage(file);
        attachment.setPost(post);
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

    public void deleteByPost(Post post) {
        List<Attachment> attachments = attachmentRepository.findByPost(post);
        for (Attachment attachment : attachments){
            imageS3Service.deleteImage(attachment);
            attachmentRepository.deleteById(attachment.getId());
        }
    }
}
