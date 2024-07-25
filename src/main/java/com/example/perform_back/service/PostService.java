package com.example.perform_back.service;

import com.example.perform_back.dto.PostDto;
import com.example.perform_back.entity.Attachment;
import com.example.perform_back.entity.Post;
import com.example.perform_back.repository.AttachmentRepository;
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

    public Post save(PostDto dto, MultipartFile file) {
        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setCategory(dto.getCategory());
        post.setContent(dto.getContent());
        post.setCreatedDate(new Date());
        Post savedPost = postRepository.save(post);
        if (file != null) {
            attachmentService.savePostWithAttachment(post, file);
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
         attachmentService.deleteByPost(post.get());
         postRepository.delete(post.get());
    }
}
