package com.msa4meerkatgram.domain.post.requests;

import jakarta.validation.constraints.NotBlank;

public record PostCreateRequest(

        // @NotBlank(message = "본문을 작성해주세요. (최소 2자 이상)")
        // @Pattern(regexp = "^[0-9a-zA-Z_]{2,1000}$")
        String content,

        @NotBlank(message = "이미지를 첨부해주세요.")
        String image
) {
}
