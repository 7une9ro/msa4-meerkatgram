package com.msa4meerkatgram.domain.post.mapper;

import com.msa4meerkatgram.domain.post.entities.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {

    List<Post> getPagination(@Param("limit") int limit, @Param("offset") int offset);

    long getTotalPosts();

    Post findById(Long id);

    long countPostsByUserId(Long id);
}
