package com.msa4meerkatgram.domain.post.services;

import com.msa4meerkatgram.domain.post.entities.Post;
import com.msa4meerkatgram.domain.post.mapper.PostMapper;
import com.msa4meerkatgram.domain.post.requests.PostCreateRequest;
import com.msa4meerkatgram.domain.post.requests.PostIndexRequest;
import com.msa4meerkatgram.domain.post.responses.PostIndexResponse;
import com.msa4meerkatgram.domain.user.entities.User;
import com.msa4meerkatgram.domain.user.mapper.UserMapper;
import com.msa4meerkatgram.global.errors.custom.InvalidTokenException;
import com.msa4meerkatgram.global.errors.custom.NotExistPostException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostMapper postMapper;
    private final UserMapper userMapper;

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

    @Transactional(rollbackFor = Exception.class)
    public Post create(PostCreateRequest postCreateRequest, long id) {

        // 유저 정보 획득
        User user = userMapper.findById(id);

        if (user == null)
            throw new InvalidTokenException("유효하지 않은 회원의 토큰입니다.");

        // 게시글 생성
        Post newPost = Post.builder()
                .userId(user.getId())
                .content(postCreateRequest.content())
                .image(postCreateRequest.image())
                .build();

        postMapper.create(newPost);

        return newPost;
    }

    public void delete(long postId, Long id) {

        // 삭제를 시도하는 유저가 존재하는 유저인가?
        User user = userMapper.findById(id);

        if (user == null)
            throw new InvalidTokenException("유효하지 않은 회원의 토큰입니다.");

        // 삭제하려는 게시글이 존재하는 게시글인가?
        Post post = postMapper.findById(postId);

        if (post == null)
            throw new NotExistPostException("존재하지 않는 게시글입니다.");

        // 게시글 삭제
        postMapper.delete(post.getId());
    }
}
