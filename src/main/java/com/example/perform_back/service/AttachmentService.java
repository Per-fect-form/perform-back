package com.example.perform_back.service;

import com.example.perform_back.entity.Attachment;
import com.example.perform_back.repository.AttachmentRepository;
import org.springframework.stereotype.Service;

@Service
public class AttachmentService {

    private AttachmentRepository attachmentRepository;

    public AttachmentService(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }
    public Attachment save(Attachment attachment) {
        return attachmentRepository.save(attachment);
    }

    public Attachment findById(Long id) {
        return attachmentRepository.findById(id).orElse(null);
    }
}
