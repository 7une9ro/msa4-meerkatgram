package com.msa4meerkatgram.domain.auth.responses;

import com.msa4meerkatgram.domain.user.entities.User;
import lombok.Builder;

@Builder
public record AuthResponse(
        User user
        , String accessToken
) {
}
