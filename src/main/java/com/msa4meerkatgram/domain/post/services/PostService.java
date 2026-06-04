package com.msa4meerkatgram.domain.post.services;

import com.msa4meerkatgram.domain.post.entities.Post;
import com.msa4meerkatgram.domain.post.mapper.PostMapper;
import com.msa4meerkatgram.domain.post.requests.PostIndexRequest;
import com.msa4meerkatgram.domain.post.responses.PostIndexResponse;
import com.msa4meerkatgram.global.errors.custom.NotExistPostException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostMapper postMapper;

    public PostIndexResponse index(PostIndexRequest postIndexRequest) {

        int offset = (postIndexRequest.page() - 1) * postIndexRequest.limit();

        // 특정 페이지 게시글 조회
        List<Post> posts = postMapper.getPagination(postIndexRequest.limit(), offset);

        // 전체 게시글 조회
        long total = postMapper.getTotalPosts();
        boolean lastPage = offset + postIndexRequest.limit() >= total;

        // 컨트롤러 전달
        return PostIndexResponse.builder()
                .total(total)
                .lastPage(lastPage)
                .posts(posts)
                .build();
    }

    public Post detail(Long id) {

        Post post = postMapper.findById(id);

        if (post == null)
            throw new NotExistPostException("존재하지 않는 게시글입니다.");

        return post;
    }
}
