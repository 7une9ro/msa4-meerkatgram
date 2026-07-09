package com.msa4meerkatgram.domain.post.repositories;

import com.msa4meerkatgram.domain.post.entities.Post;
import com.msa4meerkatgram.domain.user.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    long countByUser(User user);

    // DTO 변환 중 지연 로딩이 발생하지 않도록 게시글 작성자를 함께 조회한다.
    @EntityGraph(attributePaths = "user")
    // deleted_at IS NULL 조건은 메서드명으로 만들고, 정렬과 개수 제한은 Pageable로 전달한다.
    Page<Post> findAllByDeletedAtIsNull(Pageable pageable);
}
