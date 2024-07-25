package com.example.perform_back.controller;

import com.example.perform_back.dto.PostDto;
import com.example.perform_back.entity.Post;
import com.example.perform_back.service.PostService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/post")
@Tag(name = "Post", description = "Post API")
public class PostController {

    private PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public List<Post> getAllPosts(){
        return this.postService.findAll();
    }

    @PostMapping("/upload")
    public Post createPost(@RequestPart("post") PostDto post, @RequestPart(value = "file", required = false) MultipartFile file) {
        return this.postService.save(post, file);
    }

    @GetMapping("/{id}")
    public Post getPost(@PathVariable Long id) {
        return this.postService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void deletePostById(@PathVariable Long id) {
        this.postService.deleteById(id);
    }

}
