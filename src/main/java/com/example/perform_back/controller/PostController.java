package com.example.perform_back.controller;

import com.example.perform_back.dto.AttachmentsDto;
import com.example.perform_back.dto.PostDto;
import com.example.perform_back.service.PostService;
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
    public ResponseEntity<List<PostDto>> getAllPosts(){
        System.out.println("전체 게시글 조회");
        return ResponseEntity.status(HttpStatus.OK).body(postService.findAll());
    }

    @Operation(summary = "게시글 업로드")
    @PostMapping("/upload")
    public ResponseEntity<PostDto> createPost(@RequestPart("post") PostDto postDto,
                                              @RequestPart(value = "files", required = false) MultipartFile[] files,
                                              @RequestHeader("Authorization") String accessToken) {
        PostDto savedPost = postService.save(postDto, files, accessToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPost); //201 created
    }
    
    @Operation(summary = "특정 게시글 조회")
    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPost(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String accessToken) {
        PostDto postDto = postService.getPost(id, accessToken);
        return ResponseEntity.status(HttpStatus.OK).body(postDto);
    }

    @Operation(summary = "제목으로 게시글 조회")
    @GetMapping("/search/{title}")
    public ResponseEntity<List<PostDto>> getPostByTitle(@PathVariable String title) {
        List<PostDto> postDtoList = postService.findByTitle(title);
        return ResponseEntity.status(HttpStatus.OK).body(postDtoList);
    }

    @Operation(summary = "특정 게시글 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deletePostById(@PathVariable Long id , @RequestHeader("Authorization") String accessToken){
        this.postService.deleteById(id, accessToken);
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }

    @Operation(summary = "특정 게시글 수정")
    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePostById(@PathVariable Long id, @RequestPart("post") PostDto postDto,
                               @RequestPart(value = "attachments", required = false) AttachmentsDto attachmentsDto,
                               @RequestPart(value = "files", required = false) MultipartFile[] files,
                               @RequestHeader("Authorization") String accessToken) {
        PostDto updatePost = postService.updateById(id, postDto, attachmentsDto, files, accessToken);
        return ResponseEntity.status(HttpStatus.OK).body(updatePost);
    }

}
