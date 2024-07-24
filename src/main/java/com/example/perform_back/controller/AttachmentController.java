package com.example.perform_back.controller;

import com.example.perform_back.service.AttachmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/attachment")
@Tag(name = "Attachment", description = "Attachment API")
public class AttachmentController {

    private AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService){
        this.attachmentService = attachmentService;
    }

    @PostMapping("/upload")
    public String uploadProfileImage(@RequestParam("file") MultipartFile file) {
        return attachmentService.save(file).getPath();

    }

    @GetMapping("/{id}")
    public String getProfileImage(@PathVariable Long id) {
        return attachmentService.findById(id).getPath();
    }
}
