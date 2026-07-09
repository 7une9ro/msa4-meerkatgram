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

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostQueryRepository postQueryRepository;

    public PostIndexResponse index(PostIndexRequest postIndexRequest) {

        // // API의 1부터 시작하는 페이지 번호를 Spring Data JPA의 0부터 시작하는 페이지 번호로 변환한다.
        // PageRequest pageable = PageRequest.of(
        //         postIndexRequest.page() - 1,
        //         postIndexRequest.limit(),
        //         Sort.by(Sort.Direction.DESC, "createdAt")
        //                 .and(Sort.by(Sort.Direction.DESC, "id"))
        // );
        //
        // // 기존 MyBatis 구현은 offset = (page - 1) * limit 계산,
        // // LIMIT/OFFSET 페이지네이션, COUNT(*) WHERE deleted_at IS NULL 방식이었다.
        // Page<Post> postPage = postRepository.findAllByDeletedAtIsNull(pageable);
        //
        // // 목록 API의 공개 계약만 노출하도록 엔티티 대신 DTO로 변환한다.
        // List<PostSummaryResponse> posts = postPage.getContent().stream()
        //         .map(PostSummaryResponse::from)
        //         .toList();

        int offset = (postIndexRequest.page() - 1) * postIndexRequest.limit();

        List<Post> result = postQueryRepository.pagination(offset, postIndexRequest.limit());
        long total = postRepository.count();
        boolean lastPage = offset + postIndexRequest.limit() >= total;

        // total과 lastPage는 직접 계산하지 않고 JPA Page 메타데이터를 사용한다.
        return PostIndexResponse.from(total, lastPage, result);
    }

    public PostWithUserResponse detail(Long id) {

        Post result = postRepository.findById(id)
                .orElseThrow(() -> new NotExistPostException("議댁옱?섏? ?딅뒗 寃뚯떆湲?낅땲??"));

        return PostWithUserResponse.from(result);
    }
}
