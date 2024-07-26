package com.example.perform_back.service;

import com.example.perform_back.dto.AttachmentDto;
import com.example.perform_back.dto.AttachmentsDto;
import com.example.perform_back.dto.PostDto;
import com.example.perform_back.entity.Attachment;
import com.example.perform_back.entity.Post;
import com.example.perform_back.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private PostRepository postRepository;
    private AttachmentService attachmentService;

    @Autowired
    public PostService(PostRepository postRepository, AttachmentService attachmentService) {
        this.postRepository = postRepository;
        this.attachmentService = attachmentService;
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Post save(PostDto postDto, MultipartFile[] files) {
        Post post = convertToPost(postDto, new Post());
        Post savedPost = postRepository.save(post);

        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                attachmentService.savePostWithAttachment(post, file);
            }
            savedPost.setAttachments(attachmentService.findByPost(savedPost));
        }

        return savedPost;
    }

    public Post findById(Long id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent())
            return post.get();
        else
            throw new RuntimeException("Post not found");
    }

    public void deleteById(Long id) {
         Optional<Post> post = postRepository.findById(id);
         if(post.isEmpty())
             throw new RuntimeException("Post not found");
         attachmentService.deleteAllByPost(post.get());
         postRepository.delete(post.get());
    }

    public void updateById(Long id, PostDto postDto, AttachmentsDto attachmentsDto, MultipartFile[] files) {
        Optional<Post> post = postRepository.findById(id);
        if(post.isEmpty())
            throw new RuntimeException("Post not found");
        Post postToUpdate = convertToPost(postDto, post.get());

        if(attachmentsDto != null) {
            for(AttachmentDto attachment : attachmentsDto.getAttachments()) {
                Attachment foundAttachment = attachmentService.findById(attachment.getId());
                attachmentService.deleteById(foundAttachment);
            }
        }


        if(files != null && files.length > 0) {
            for (MultipartFile file : files){
                attachmentService.savePostWithAttachment(postToUpdate, file);
            }
            postToUpdate.setAttachments(attachmentService.findByPost(postToUpdate));
        }

        postRepository.save(postToUpdate);
    }

    private static Post convertToPost(PostDto postDto, Post post) {
        post.setTitle(postDto.getTitle());
        post.setCategory(postDto.getCategory());
        post.setContent(postDto.getContent());
        post.setCreatedDate(new Date());
        return post;
    }
}
