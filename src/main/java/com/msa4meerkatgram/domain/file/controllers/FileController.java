package com.msa4meerkatgram.domain.file.controllers;

import com.msa4meerkatgram.domain.file.responses.FileResponse;
import com.msa4meerkatgram.domain.file.services.FileService;
import com.msa4meerkatgram.global.config.openapi.CustomApiResponse;
import com.msa4meerkatgram.global.responses.BaseResponse;
import com.msa4meerkatgram.global.responses.constant.CustomResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "파일 API", description = "파일 업로드 관련")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FileController {

    private final FileService fileService;

    @Operation(summary = "프로필 이미지 업로드 처리")
    @CustomApiResponse(value = {
        CustomResponseCode.FILE_MANAGED_ERROR
        , CustomResponseCode.SYSTEM_ERROR
    })
    @PostMapping("/files/profiles")
    public ResponseEntity<BaseResponse<FileResponse>> storeProfile(
        @ModelAttribute MultipartFile file
    ) {
        return ResponseEntity.ok(BaseResponse.success(fileService.storeProfile(file)));
    }

    @Operation(summary = "게시글 이미지 업로드 처리")
    @CustomApiResponse(value = {
        CustomResponseCode.FILE_MANAGED_ERROR
        , CustomResponseCode.SYSTEM_ERROR
    })
    @PostMapping("/files/posts")
    public ResponseEntity<BaseResponse<FileResponse>> storePost(
            @ModelAttribute MultipartFile file
    ) {
        return ResponseEntity.ok(BaseResponse.success(fileService.storePost(file)));
    }
}
