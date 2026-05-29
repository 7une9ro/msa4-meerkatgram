package com.msa4meerkatgram.domain.auth.responses;

import com.msa4meerkatgram.domain.user.responses.UserResponse;
import lombok.Builder;

@Builder
public record AuthResponse(
        UserResponse user
        , String accessToken
) {
}
