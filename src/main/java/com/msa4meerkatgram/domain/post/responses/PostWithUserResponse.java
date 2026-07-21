package com.msa4meerkatgram.domain.post.responses;

import com.msa4meerkatgram.domain.post.entities.Post;
import com.msa4meerkatgram.domain.user.responses.UserResponse;

import java.time.LocalDateTime;

public record PostWithUserResponse(
        Long id
        , UserResponse user
        , String content
        , String image
        , LocalDateTime createdAt
        , LocalDateTime updatedAt
        , LocalDateTime deletedAt
) {
    public static PostWithUserResponse from(Post post) {
        return new PostWithUserResponse(
                post.getId()
                , UserResponse.from(post.getUser())
                , post.getContent()
                , post.getImage()
                , post.getCreatedAt()
                , post.getUpdatedAt()
                , post.getDeletedAt()
        );
    }
}
