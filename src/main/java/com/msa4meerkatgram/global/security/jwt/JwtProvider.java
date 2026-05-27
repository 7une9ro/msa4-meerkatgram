package com.msa4meerkatgram.global.security.jwt;

import com.msa4meerkatgram.domain.user.entities.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {

    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;

    public JwtProvider(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtConfig.secret()));
    }

    private String generateToken(User user, long ttl) {
        Date now = new Date();

        return Jwts.builder()
                .header()                                       // 헤더 세팅
                .type(jwtConfig.type())                         // 토큰 유형 설정
                .and()
                .subject(String.valueOf(user.getId()))          // 토큰 주체자 (토큰을 가진 사용자)
                .issuer(jwtConfig.issuer())                     // 토큰 발급자
                .issuedAt(now)                                  // 토큰 발급 시간
                .expiration(new Date(now.getTime() + ttl))      // 만료 시간
                .claim("role", user.getRole())            // private claim 설정
                .signWith(secretKey)                            // 시그니처 작성
                .compact();
    }

    // 액세스 토큰 생성
    public String generateAccessToken(User user) {
        return generateToken(user, jwtConfig.accessTokenExpiry());
    }

    // 리프레시 토큰 생성 (액세스 토큰 만료)
    public String generateRefreshToken(User user) {
        return generateToken(user, jwtConfig.refreshTokenExpiry());
    }
}
