package com.msa4meerkatgram.domain.post.controllers;

import com.msa4meerkatgram.domain.post.requests.PostIndexRequest;
import com.msa4meerkatgram.domain.post.responses.PostIndexResponse;
import com.msa4meerkatgram.domain.post.responses.PostWithUserResponse;
import com.msa4meerkatgram.domain.post.services.PostService;
import com.msa4meerkatgram.global.config.openapi.CustomApiResponse;
import com.msa4meerkatgram.global.responses.BaseResponse;
import com.msa4meerkatgram.global.responses.constant.CustomResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "게시글 API", description = "게시글 관련")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @Operation(summary = "게시글 목록 조회 처리")
    @CustomApiResponse(value = {
        CustomResponseCode.INVALID_PARAMETER_ERROR
        , CustomResponseCode.DB_ERROR
        , CustomResponseCode.SYSTEM_ERROR
    })
    @GetMapping("/posts")
    public ResponseEntity<BaseResponse<PostIndexResponse>> index(@Valid PostIndexRequest postIndexRequest) {

        return ResponseEntity.ok(BaseResponse.success(postService.index(postIndexRequest)));
    }

    @Operation(summary = "게시글 상세 조회 처리")
    @CustomApiResponse(value = {
        CustomResponseCode.INVALID_PARAMETER_ERROR
        , CustomResponseCode.UNAUTHENTICATED_ERROR
        , CustomResponseCode.INVALID_TOKEN_ERROR
        , CustomResponseCode.DB_ERROR
        , CustomResponseCode.SYSTEM_ERROR
    })
    @GetMapping("/posts/{id}")
    public ResponseEntity<BaseResponse<PostWithUserResponse>> detail(
            @Parameter(description = "게시글 번호", example = "1")
            @Min(value = 1, message = "1이상 숫자만 가능합니다.")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(BaseResponse.success(postService.detail(id)));
    }
}
