package com.example.perform_back.controller;

import com.example.perform_back.entity.Attachment;
import com.example.perform_back.global.service.ImageS3Service;
import com.example.perform_back.service.AttachmentService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/attachment")
public class AttachmentController {

    private ImageS3Service imageS3Service;
    private AttachmentService attachmentService;

    public AttachmentController(ImageS3Service imageS3Service, AttachmentService attachmentService){
        this.imageS3Service = imageS3Service;
        this.attachmentService = attachmentService;
    }

    @PostMapping("/upload")
    public String uploadProfileImage(@RequestParam("file") MultipartFile file) {

        // 이미지 업로드 및 URL 생성
        Attachment attachment = imageS3Service.uploadImage(file);
        attachment = attachmentService.save(attachment);

        return attachment.getPath();
    }

    @GetMapping("/{id}")
    public String getProfileImage(@PathVariable Long id) {
        Attachment attachment = attachmentService.findById(id);
        if (attachment == null) {
           throw new RuntimeException("attachment");
        }
        return attachment.getPath();
    }
}
