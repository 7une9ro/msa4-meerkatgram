package com.msa4meerkatgram.global.security.jwt;

import com.msa4meerkatgram.domain.user.entities.User;
import com.msa4meerkatgram.global.errors.custom.InvalidTokenException;
import com.msa4meerkatgram.global.security.cookie.CookieManager;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtProvider {

    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;
    private final CookieManager cookieManager;

    public JwtProvider(JwtConfig jwtConfig, CookieManager cookieManager) {
        this.jwtConfig = jwtConfig;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtConfig.secret()));
        this.cookieManager = cookieManager;
    }


    // 액세스 토큰 생성
    public String generateAccessToken(User user) {
        return generateToken(user, jwtConfig.accessTokenExpiry());
    }

    // 리프레시 토큰 생성 (액세스 토큰 만료)
    public String generateRefreshToken(User user) {
        return generateToken(user, jwtConfig.refreshTokenExpiry());
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

    // 쿠키에서 refreshToken 추출
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return cookieManager.getCookie(request, jwtConfig.refreshTokenCookieName())
                .map(Cookie::getValue);
    }

    // 토큰 검증 및 Claim(Payload) 추출
    public Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(this.secretKey)
                    .build()
                    .parseSignedClaims(token) // JWT signature 검증 및 Claim(Payload) 추출
                    .getPayload();
        } catch (ExpiredJwtException e) { // 토큰 만료 에러
            throw new InvalidTokenException("토큰이 만료되었습니다.");
        } catch (UnsupportedJwtException e) {
            throw new InvalidTokenException("토큰 서명이 위조되었습니다.");
        } catch (MalformedJwtException e) {
            throw new InvalidTokenException("토큰 형식이 올바르지 않습니다.");
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("토큰 검증에 실패했습니다.");
        }
    }
}
