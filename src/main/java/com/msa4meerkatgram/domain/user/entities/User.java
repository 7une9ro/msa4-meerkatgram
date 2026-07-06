package com.msa4meerkatgram.domain.user.entities;

import com.msa4meerkatgram.domain.common.BaseTimeEntity;
import com.msa4meerkatgram.global.security.constant.ProviderPolicy;
import com.msa4meerkatgram.global.security.constant.RolePolicy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Types;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class) // 엔티티의 이벤트 리스터 지정
@Table(name = "users") // 테이블명 매핑
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() where id = ?")
@SQLRestriction("deleted_at IS NULL") // 엔티티 조회 시 항상 특정 조건을 추가하도록 지정
@Getter
@Setter
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PK 자동 생성 전략 설정
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    private long id;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "nick", length = 20, nullable = false)
    private String nick;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", length = 10, nullable = false)
    @JdbcTypeCode(Types.VARCHAR)
    private ProviderPolicy provider = ProviderPolicy.NONE;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 10, nullable = false)
    @JdbcTypeCode(Types.VARCHAR)
    private RolePolicy role = RolePolicy.NORMAL;

    @Column(name = "profile", length = 100, nullable = false)
    private String profile;

    @Column(name = "refresh_token", nullable = true, length = 255)
    private String refreshToken;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
