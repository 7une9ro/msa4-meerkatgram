package com.msa4meerkatgram.domain.post.responses;

import com.msa4meerkatgram.domain.post.entities.Post;

import java.util.List;

public record PostIndexResponse(
        long total
        , boolean lastPage
        , List<PostWithUserResponse> posts
) {
    public static PostIndexResponse from(long total, boolean lastPage, List<Post> posts) {
        return new PostIndexResponse(
                total
                , lastPage
                , posts.stream().map(PostWithUserResponse::from).toList()
        );
    }
}
