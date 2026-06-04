package com.msa4meerkatgram.domain.post.controllers;

import com.msa4meerkatgram.domain.post.entities.Post;
import com.msa4meerkatgram.domain.post.requests.PostIndexRequest;
import com.msa4meerkatgram.domain.post.responses.PostIndexResponse;
import com.msa4meerkatgram.domain.post.services.PostService;
import com.msa4meerkatgram.global.responses.BaseResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/posts")
    public ResponseEntity<BaseResponse<PostIndexResponse>> index(@Valid PostIndexRequest postIndexRequest) {

        return ResponseEntity.status(200).body(
                BaseResponse.<PostIndexResponse>builder()
                        .code("00")
                        .message("정상 처리")
                        .data(postService.index(postIndexRequest))
                        .build()
        );
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<BaseResponse<Post>> detail(
            @Min(value = 1, message = "1이상 숫자만 가능합니다.")
            @PathVariable Long id
    ) {
        return ResponseEntity.status(200).body(
                BaseResponse.<Post>builder()
                        .code("00")
                        .message("정상 처리")
                        .data(postService.detail(id))
                        .build()
        );
    }
}
