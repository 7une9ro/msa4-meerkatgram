package com.msa4meerkatgram.domain.post.services;

import com.msa4meerkatgram.domain.post.entities.Post;
import com.msa4meerkatgram.domain.post.repositories.PostQueryRepository;
import com.msa4meerkatgram.domain.post.repositories.PostRepository;
import com.msa4meerkatgram.domain.post.requests.PostIndexRequest;
import com.msa4meerkatgram.domain.post.responses.PostIndexResponse;
import com.msa4meerkatgram.domain.post.responses.PostWithUserResponse;
import com.msa4meerkatgram.global.errors.custom.NotExistPostException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostQueryRepository postQueryRepository;

    @Transactional(rollbackFor = Exception.class)
    public PostIndexResponse index(PostIndexRequest postIndexRequest) {

        int offset = (postIndexRequest.page() - 1) * postIndexRequest.limit();

        List<Post> result = postQueryRepository.pagination(offset, postIndexRequest.limit());

        long total = postRepository.count();
        boolean lastPage = offset + postIndexRequest.limit() >= total;

        // total과 lastPage는 직접 계산하지 않고 JPA Page 메타데이터를 사용한다.
        return PostIndexResponse.from(total, lastPage, result);
    }

    @Transactional(rollbackFor = Exception.class)
    public PostWithUserResponse detail(Long id) {

        Post result = postRepository.findById(id)
                .orElseThrow(() -> new NotExistPostException("존재하지 않는 게시물 입니다."));

        return PostWithUserResponse.from(result);
    }
}
