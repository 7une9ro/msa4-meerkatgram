package com.msa4meerkatgram.domain.user.responses;

import com.msa4meerkatgram.domain.user.entities.User;
import com.msa4meerkatgram.global.security.constant.RolePolicy;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String email,
        String nick,
        RolePolicy role,
        String profile,
        LocalDateTime createdAt
) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId()
                , user.getEmail()
                , user.getNick()
                , user.getRole()
                , user.getProfile()
                , user.getCreatedAt()
        );
    }
}
