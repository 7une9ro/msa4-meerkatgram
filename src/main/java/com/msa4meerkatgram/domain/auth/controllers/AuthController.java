package com.msa4meerkatgram.domain.auth.controllers;

import com.msa4meerkatgram.domain.auth.requests.LoginRequest;
import com.msa4meerkatgram.domain.auth.requests.RegistrationRequest;
import com.msa4meerkatgram.domain.auth.responses.AuthResponse;
import com.msa4meerkatgram.domain.auth.services.AuthService;
import com.msa4meerkatgram.global.responses.BaseResponse;
import io.jsonwebtoken.Claims;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {


    private final AuthService authService;

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
