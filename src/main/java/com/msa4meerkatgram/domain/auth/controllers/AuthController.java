package com.msa4meerkatgram.domain.auth.controllers;

import com.msa4meerkatgram.domain.auth.requests.LoginRequest;
import com.msa4meerkatgram.domain.auth.requests.RegistrationRequest;
import com.msa4meerkatgram.domain.auth.responses.AuthResponse;
import com.msa4meerkatgram.domain.auth.services.AuthService;
import com.msa4meerkatgram.global.config.openapi.CustomApiResponse;
import com.msa4meerkatgram.global.responses.BaseResponse;
import com.msa4meerkatgram.global.responses.constant.CustomResponseCode;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
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
    @CustomApiResponse(value = {
        CustomResponseCode.NOT_REGISTERED_ERROR
        , CustomResponseCode.INVALID_PARAMETER_ERROR
        , CustomResponseCode.DB_ERROR
        , CustomResponseCode.SYSTEM_ERROR
    })
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest
            , HttpServletResponse response
    ) {
        return ResponseEntity.ok(BaseResponse.success(authService.login(loginRequest, response)));
    }

    @Operation(summary = "토큰 재발급 처리")
    @CustomApiResponse(value = {
        CustomResponseCode.INVALID_TOKEN_ERROR
        , CustomResponseCode.DB_ERROR
        , CustomResponseCode.SYSTEM_ERROR
    })
    @PostMapping("/reissue-token")
    public ResponseEntity<BaseResponse<AuthResponse>> reissue(
            HttpServletRequest request
            ,HttpServletResponse response
    ) {
        return ResponseEntity.ok(BaseResponse.success(authService.reissue(request, response)));
    }

    @Operation(summary = "로그아웃 처리")
    @CustomApiResponse(value = {
            CustomResponseCode.UNAUTHENTICATED_ERROR
            , CustomResponseCode.INVALID_TOKEN_ERROR
            , CustomResponseCode.DB_ERROR
            , CustomResponseCode.SYSTEM_ERROR
    })
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout(
            HttpServletResponse response
            , @AuthenticationPrincipal Claims claims
            ) {
        authService.logout(response, Long.parseLong(claims.getSubject()));
        return ResponseEntity.ok(BaseResponse.success());
    }

    @Operation(summary = "회원가입 처리")
    @CustomApiResponse(value = {
            CustomResponseCode.INVALID_TOKEN_ERROR
            , CustomResponseCode.DB_ERROR
            , CustomResponseCode.SYSTEM_ERROR
    })
    @PostMapping("/registration")
    public ResponseEntity<BaseResponse<Void>> registration(
            @Valid @RequestBody RegistrationRequest registrationRequest
            ) {
        authService.registration(registrationRequest);
        return ResponseEntity.ok(BaseResponse.success());
    }
}
