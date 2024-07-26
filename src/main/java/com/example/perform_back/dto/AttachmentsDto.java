package com.example.perform_back.dto;

import com.example.perform_back.entity.Attachment;
import lombok.Data;

import java.util.List;

@Data
public class AttachmentsDto {
    List<AttachmentDto> attachments;
}
