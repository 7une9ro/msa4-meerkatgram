package com.msa4meerkatgram.domain.auth.controllers;

import com.msa4meerkatgram.domain.auth.requests.LoginRequest;
import com.msa4meerkatgram.domain.auth.requests.RegistrationRequest;
import com.msa4meerkatgram.domain.auth.responses.AuthResponse;
import com.msa4meerkatgram.domain.auth.services.AuthService;
import com.msa4meerkatgram.global.annotations.openapi.ApiNotValidErrorResponse;
import com.msa4meerkatgram.global.annotations.openapi.ApiUnauthenticatedErrorResponse;
import com.msa4meerkatgram.global.responses.BaseResponse;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// @Tag: API들을 기능별 또는 도메인별로 그룹화 할 때 사용
@Tag(name = "인증 API", description = "인증 및 인가 담당")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {


    private final AuthService authService;

    @Operation(summary = "로그인 처리", description = "이메일과 비밀번호로 로그인")
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    @ApiNotValidErrorResponse
    @ApiUnauthenticatedErrorResponse
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest
            , HttpServletResponse response
    ) {

        return ResponseEntity.status(200).body(
                BaseResponse.<AuthResponse>builder()
                        .code("00")
                        .message("정상 처리")
                        .data(authService.login(loginRequest, response))
                        .build()
        );
    }

    @ApiUnauthenticatedErrorResponse
    @PostMapping("/reissue-token")
    public ResponseEntity<BaseResponse<AuthResponse>> reissue(
            HttpServletRequest request
            ,HttpServletResponse response
    ) {
        return ResponseEntity.status(200).body(
                BaseResponse.<AuthResponse>builder()
                        .code("00")
                        .message("토큰 재발급 성공")
                        .data(authService.reissue(request, response))
                        .build()
        );
    }

    @ApiUnauthenticatedErrorResponse
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<String>> logout(
            HttpServletResponse response
            , @AuthenticationPrincipal Claims claims
            ) {
        authService.logout(response, Long.parseLong(claims.getSubject()));

        return ResponseEntity.status(200).body(
                BaseResponse.<String>builder()
                        .code("00")
                        .message("로그아웃 성공")
                        .build()
        );
    }

    @PostMapping("/registration")
    public ResponseEntity<BaseResponse<String>> registration(
            @Valid @RequestBody RegistrationRequest registrationRequest
            ) {
        authService.registration(registrationRequest);

        return ResponseEntity.status(200).body(
                BaseResponse.<String>builder()
                        .code("00")
                        .message("회원가입 성공")
                        .build()
        );
    }
}
