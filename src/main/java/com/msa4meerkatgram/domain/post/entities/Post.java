package com.msa4meerkatgram.domain.post.entities;

import com.msa4meerkatgram.domain.common.BaseTimeEntity;
import com.msa4meerkatgram.domain.user.entities.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "posts")
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() where id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
public class Post extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PK 자동 생성 전략 설정
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_id"
        , insertable = true  // insert 할 때, User 객체에 어떤 값을 넣더라도, insert 문에 `user_id` 컬럼을 포함하겠다.
        , updatable = false   // update 할 때, User 객체에 어떤 값을 넣더라도, update 문에 `user_id` 컬럼을 포함하지 않겠다.
        , nullable = false
        , foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)  // <- 물리적 FK 생성하고 싶지 않을 때
    )
    private User user;

    @Column(name = "content", length = 200, nullable = false)
    private String content;

    @Column(name = "image", length = 100, nullable = false)
    private String image;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
