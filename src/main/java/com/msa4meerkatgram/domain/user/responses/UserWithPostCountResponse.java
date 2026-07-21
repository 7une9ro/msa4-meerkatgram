package com.msa4meerkatgram.domain.user.responses;

import com.msa4meerkatgram.domain.user.entities.User;

public record UserWithPostCountResponse(
        UserResponse user
        , long countPosts
) {

    public static UserWithPostCountResponse from(User user, long countPosts) {
        return new UserWithPostCountResponse(
                UserResponse.from(user)
                , countPosts
        );
    }
}
