package com.msa4meerkatgram.global.security.cookie;

import com.msa4meerkatgram.global.security.jwt.JwtConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CookieManager {

    private final JwtConfig jwtConfig;

    /**
     * Request Header에서 특정 쿠키를 획득하는 메서드 (Optional 반환)
     * @param request 요청
     * @param name 찾고자하는 쿠키명
     * @return Optional<Cookie>
     * @throws Exception
     */
    public Optional<Cookie> getCookie(HttpServletRequest request, String name) {

        // 쿠키 존재 여부 확인
        if (request.getCookies() == null)
            return Optional.empty();

        // name과 일치하는 쿠키 획득
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(name))
                .findFirst();
    }

    // 쿠키 생성 메서드
    public void setCookie(HttpServletResponse response, String name, String value, int maxAge, String path) {

        Cookie cookie = new Cookie(name, value);
        cookie.setPath(path);                       // 쿠키를 사용할 path 설정
        cookie.setMaxAge(maxAge);                   // 쿠키 유효시간 설정
        cookie.setHttpOnly(true);                   // XSS 공격 방지 설정
        cookie.setSecure(jwtConfig.secure());       // 보안 설정 (MITM 공격 방지)

        response.addCookie(cookie);                 // 응답 헤더에 완성된 쿠키 추가
    }
}
