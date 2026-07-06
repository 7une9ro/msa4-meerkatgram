package com.msa4meerkatgram.domain.auth.services;

import com.msa4meerkatgram.domain.auth.mapper.AuthMapper;
import com.msa4meerkatgram.domain.auth.requests.LoginRequest;
import com.msa4meerkatgram.domain.auth.requests.RegistrationRequest;
import com.msa4meerkatgram.domain.auth.responses.AuthResponse;
import com.msa4meerkatgram.domain.post.mapper.PostMapper;
import com.msa4meerkatgram.domain.user.entities.User;
import com.msa4meerkatgram.domain.user.mapper.UserMapper;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final JwtProvider jwtProvider;
    private final AuthMapper authMapper;
    private final CookieManager cookieManager;
    private final JwtConfig jwtConfig;
    private final PasswordEncoder passwordEncoder;
    private final PostMapper postMapper;

    @Transactional(rollbackFor = Exception.class)
    public AuthResponse login(LoginRequest loginRequest, HttpServletResponse response) {

        // 유저 정보 획득
        User user = userMapper.findByEmail(loginRequest.email());

        // 유저 가입 여부 확인
        if (user == null) {
            throw new NotRegisteredException("아이디와 비밀번호를 다시 확인해주세요.");
        }

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
        // HttpServletRequest 객체로부터 refreshToken 획득(추출)
        Optional<String> extractedRefreshToken = jwtProvider.extractRefreshToken(request);

        // 쿠키 존재 유무 검증
        if (extractedRefreshToken.isEmpty())
            throw new InvalidTokenException("토큰이 존재하지 않습니다.");

        String refreshToken = extractedRefreshToken.get();

        long subjectId = Long.parseLong(jwtProvider.extractClaims(refreshToken).getSubject());

        // 유저 획득
        User user = userMapper.findById(subjectId);

        // 유저 가입 여부 확인
        if (user == null)
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
        long countPosts = postMapper.countPostsByUserId(user.getId());

        // 토큰 생성
        String newAccessToken = jwtProvider.generateAccessToken(user);
        String newRefreshToken = jwtProvider.generateRefreshToken(user);

        // refresh 토큰 -> DB 저장
        authMapper.updateRefreshToken(user.getId(), newRefreshToken);

        // refresh 토큰 -> Cookie에 저장
        cookieManager.setCookie(
                response
                , jwtConfig.refreshTokenCookieName()
                , newRefreshToken
                , jwtConfig.refreshTokenCookieExpiry()
                , jwtConfig.reissUri());

        // 리턴
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                // .user(
                //         UserResponse.builder()
                //                 .id(user.getId())
                //                 .email(user.getEmail())
                //                 .nick(user.getNick())
                //                 .role(user.getRole())
                //                 .profile(user.getProfile())
                //                 .createdAt(user.getCreatedAt())
                //                 .countPosts(countPosts)
                //                 .build()
                // )
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public void logout(HttpServletResponse response, long id) {

        // 유저 정보 획득
        User user = userMapper.findById(id);

        if (user == null)
            throw new InvalidTokenException("유효하지 않은 회원의 토큰입니다.");

        // DB에 저장된 refresh 토큰 파기
        authMapper.updateRefreshToken(user.getId(), null);

        // Cookie에 저장된 refresh 토큰 파기
        cookieManager.setCookie(
                response
                , jwtConfig.refreshTokenCookieName()
                , null
                , 0
                , jwtConfig.reissUri()
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public void registration(RegistrationRequest registrationRequest) {
        // 회원가입할 유저의 정보가 DB에 이미 존재하는지 확인하기 위한 email 조회
        User user = userMapper.findByEmail(registrationRequest.email());

        if (user != null) {
            throw new DuplicatedRecordException("이미 가입된 회원입니다.");
        }

        // User newUser = User.builder()
        //         .email(registrationRequest.email())
        //         .password(passwordEncoder.encode(registrationRequest.password()))
        //         .nick(registrationRequest.nick())
        //         .provider(ProviderPolicy.NONE.getProvider())
        //         .role(RolePolicy.NORMAL.getRole())
        //         .profile(registrationRequest.profile())
        //         .build();

        // authMapper.create(newUser);
    }
}
