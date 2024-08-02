package com.example.perform_back.controller;

import com.example.perform_back.dto.AttachmentsDto;
import com.example.perform_back.dto.PostDto;
import com.example.perform_back.entity.Post;
import com.example.perform_back.service.PostService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Operation(summary = "전체 게시글 조회")
    @GetMapping
    public List<PostDto> getAllPosts(){
        return this.postService.convertToPostDtoList(postService.findAll());
    }

    @Operation(summary = "게시글 업로드")
    @PostMapping("/upload")

    public ResponseEntity<PostDto> createPost(@RequestPart("post") PostDto postDto, @RequestPart(value = "files", required = false) MultipartFile[] files,
                                              @RequestHeader("Authorization") String accessToken) throws JsonProcessingException {
        PostDto savedPost = postService.save(postDto, files, accessToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPost); //201 created
    }
    
    @Operation(summary = "특정 게시글 조회")
    @GetMapping("/{id}")
    public PostDto getPost(@PathVariable Long id) {
        return postService.converToPostDto( postService.findById(id));
    }

    @Operation(summary = "제목으로 게시글 조회")
    @GetMapping("/search/{title}")
    public List<Post> getPostByTitle(@PathVariable String title) {
        return this.postService.findByTitle(title);
    }

    @Operation(summary = "특정 게시글 삭제")
    @DeleteMapping("/{id}")
    public void deletePostById(@PathVariable Long id){
        this.postService.deleteById(id);
    }

    @Operation(summary = "특정 게시글 수정")
    @PutMapping("/{id}")
    public Post updatePostById(@PathVariable Long id, @RequestPart("post") PostDto postDto,
                               @RequestPart(value = "attachments", required = false) AttachmentsDto attachmentsDto,
                               @RequestPart(value = "files", required = false) MultipartFile[] files) {
        return  postService.updateById(id, postDto, attachmentsDto, files);
    }

}
