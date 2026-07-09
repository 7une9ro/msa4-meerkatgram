package com.msa4meerkatgram.domain.file.controllers;

import com.msa4meerkatgram.domain.file.responses.FileResponse;
import com.msa4meerkatgram.domain.file.services.FileService;
import com.msa4meerkatgram.global.responses.BaseResponse;
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

    @PostMapping("/files/profiles")
    public ResponseEntity<BaseResponse<FileResponse>> storeProfile(
            @ModelAttribute MultipartFile file
    ) {
        return ResponseEntity.status(200).body(
                BaseResponse.<FileResponse>builder()
                        .code("00")
                        .message("프로필 파일 정상 업로드")
                        .data(fileService.storeProfile(file))
                        .build()
        );
    }

    @PostMapping("/files/posts")
    public ResponseEntity<BaseResponse<FileResponse>> storePost(
            @ModelAttribute MultipartFile file
    ) {
        return ResponseEntity.status(200).body(
                BaseResponse.<FileResponse>builder()
                        .code("00")
                        .message("게시물 파일 정상 업로드")
                        .data(fileService.storePost(file))
                        .build()
        );
    }
}
