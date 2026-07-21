package com.msa4meerkatgram.domain.auth.services;

import com.msa4meerkatgram.domain.auth.repositories.AuthRepository;
import com.msa4meerkatgram.domain.auth.requests.LoginRequest;
import com.msa4meerkatgram.domain.auth.requests.RegistrationRequest;
import com.msa4meerkatgram.domain.auth.responses.AuthResponse;
import com.msa4meerkatgram.domain.post.repositories.PostRepository;
import com.msa4meerkatgram.domain.user.entities.User;
import com.msa4meerkatgram.domain.user.repositories.UserRepository;
import com.msa4meerkatgram.global.errors.custom.DuplicatedRecordException;
import com.msa4meerkatgram.global.errors.custom.InvalidTokenException;
import com.msa4meerkatgram.global.errors.custom.NotRegisteredException;
import com.msa4meerkatgram.global.security.cookie.CookieManager;
import com.msa4meerkatgram.global.security.jwt.JwtConfig;
import com.msa4meerkatgram.global.security.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final CookieManager cookieManager;
    private final JwtConfig jwtConfig;
    private final PasswordEncoder passwordEncoder;
    private final PostRepository postRepository;
    private final AuthRepository authRepository;

    @Transactional(rollbackFor = Exception.class)
    public AuthResponse login(LoginRequest loginRequest, HttpServletResponse response) {

        // 유저 정보 획득 + 유저 가입 여부 확인
        User user = authRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new NotRegisteredException("아이디와 비밀번호를 다시 확인해주세요."));

        // 비밀번호 체크
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword()))
            throw new NotRegisteredException("아이디와 비밀번호를 다시 확인해주세요.");

        return this.generateAuthentication(response, user);
    }

    /**
     * refresh 토큰 재발급
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return AuthResponse
     */
    @Transactional(rollbackFor = Exception.class)
    public AuthResponse reissue(HttpServletRequest request, HttpServletResponse response) {
        // HttpServletRequest 객체로부터 refreshToken 획득(추출) + 쿠키 존재 유무 검증
        String refreshToken = jwtProvider.extractRefreshToken(request)
                .orElseThrow(() -> new InvalidTokenException("토큰이 존재하지 않습니다."));

        long subjectId = Long.parseLong(jwtProvider.extractClaims(refreshToken).getSubject());

        // 유저 획득 + 유저 가입 여부 확인
        User user = authRepository.findById(subjectId)
                .orElseThrow(() -> new InvalidTokenException("유효하지 않은 회원의 토큰입니다."));

        if(user.getRefreshToken() == null)
            throw new InvalidTokenException("유효하지 않은 회원의 토큰입니다.");

        // DB에 저장되어 있는 refreshToken과 요청 쿠키에 저장되어 있는 refreshToken과 비교
        if (!user.getRefreshToken().equals(refreshToken))
            throw new InvalidTokenException("토큰이 일치하지 않습니다.");

        return this.generateAuthentication(response, user);
    }

    /**
     * Access & Refresh 토큰 생성 및 Refresh 토큰 -> DB & 쿠키에 저장
     * @param response HttpServletResponse
     * @param user User
     * @return AuthResponse
     */
    private AuthResponse generateAuthentication(HttpServletResponse response, User user) {
        long countPosts = postRepository.countByUser(user);

        // 토큰 생성
        String newAccessToken = jwtProvider.generateAccessToken(user);
        String newRefreshToken = jwtProvider.generateRefreshToken(user);

        // refresh 토큰 -> DB 저장
        user.setRefreshToken(newRefreshToken);
        authRepository.save(user);

        // refresh 토큰 -> Cookie에 저장
        cookieManager.setCookie(
                response
                , jwtConfig.refreshTokenCookieName()
                , newRefreshToken
                , jwtConfig.refreshTokenCookieExpiry()
                , jwtConfig.reissueUri());

        // 리턴
        return AuthResponse.from(user, newAccessToken, countPosts);
    }

    @Transactional(rollbackFor = Exception.class)
    public void logout(HttpServletResponse response, long id) {

        // 유저 정보 획득
        User user = authRepository.findById(id)
                .orElseThrow(() -> new InvalidTokenException("유효하지 않은 회원의 토큰입니다."));

        // DB에 저장된 refresh 토큰 파기
        user.setRefreshToken(null);
        authRepository.save(user);

        // Cookie에 저장된 refresh 토큰 파기
        cookieManager.setCookie(
                response
                , jwtConfig.refreshTokenCookieName()
                , null
                , 0
                , jwtConfig.reissueUri()
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public void registration(RegistrationRequest registrationRequest) {
        // 회원가입할 유저의 정보가 DB에 이미 존재하는지 확인하기 위한 email 조회

        // 유저 가입 여부 확인 (exists 쿼리를 사용하면 대용량 환경에서 효율이 증가)
        if (authRepository.existsByEmail(registrationRequest.email()))
            throw new DuplicatedRecordException("이미 가입된 회원입니다.");

        User newUser = new User();
        newUser.setEmail(registrationRequest.email());
        newUser.setPassword(passwordEncoder.encode(registrationRequest.password()));
        newUser.setNick(registrationRequest.nick());
        // newUser.setProvider(ProviderPolicy.NONE);
        // newUser.setRole(RolePolicy.NORMAL);
        newUser.setProfile(registrationRequest.profile());

        userRepository.save(newUser);
    }
}
