package com.msa4meerkatgram.domain.auth.responses;

import com.msa4meerkatgram.domain.user.entities.User;
import com.msa4meerkatgram.domain.user.responses.UserWithPostCountResponse;

public record AuthResponse(
        UserWithPostCountResponse user
        , String accessToken
) {

    public static AuthResponse from(User user, String accessToken, long countPosts) {
        return new AuthResponse(
                UserWithPostCountResponse.from(user, countPosts)
                , accessToken
        );
    }
}
