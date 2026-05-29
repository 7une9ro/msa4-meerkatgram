package com.msa4meerkatgram.domain.user.controllers;

import com.msa4meerkatgram.domain.user.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // @GetMapping("/test")
    // public ResponseEntity<BaseResponse<AuthResponse>> test() {
    //
    //     return ResponseEntity.status(200).body(
    //             BaseResponse.<AuthResponse>builder()
    //                     .code("00")
    //                     .message("정상 처리")
    //                     .data(userService.test())
    //                     .build()
    //     );
    // }
}
