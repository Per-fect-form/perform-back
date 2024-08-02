package com.example.perform_back.dto;

import com.example.perform_back.entity.Attachment;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private String category;
    private Long userId;
    private Date createdDate;

    private List<AttachmentDto> attachments;
    private List<CommentDto> comments;
    private int likes;

    @Builder
    public PostDto(Long id, String title, String content, String category, Long userId, Date createdDate, List<AttachmentDto> attachments, List<CommentDto> comments, int likes) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.category = category;
        this.userId = userId;
        this.createdDate = createdDate;
        this.attachments = attachments;
        this.comments = comments;
        this.likes = likes;
    }

}
