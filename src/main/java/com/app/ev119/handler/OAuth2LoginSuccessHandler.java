package com.app.ev119.handler;

import com.app.ev119.domain.entity.Member;
import com.app.ev119.jwt.JwtTokenProvider;
import com.app.ev119.oauth2.OAuth2MemberInfo;
import com.app.ev119.oauth2.OAuth2MemberInfoFactory;
import com.app.ev119.repository.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final StringRedisTemplate stringRedisTemplate;

    @Value("${app.frontend.redirect-url}")
    private String frontendRedirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        log.info("OAuth2 로그인 성공 authentication: {}", authentication);


        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();

        String registrationId = oauthToken.getAuthorizedClientRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2MemberInfo oauth2MemberInfo = OAuth2MemberInfoFactory.of(registrationId, attributes);

        String email = oauth2MemberInfo.getEmail();
        log.info("oauth2 로그인 email: {}", email);

        if (email == null || email.isBlank()) {
            redirectWithError(request, response, "OAUTH_EMAIL_MISSING");
            return;
        }


        Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("소셜로그인이 회원 DB에 없습니다."));


        if (!isRedisAvailable()) {
            log.error("Redis is not available. Block OAuth2 login. memberId={}", member.getId());
            redirectWithError(request, response, "REDIS_DOWN");
            return;
        }


        String role = "ROLE_" + member.getMemberType().name();
        String accessToken = jwtTokenProvider.createAccessToken(member.getId(), role);


        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());

        long refreshTokenExpireMs = jwtTokenProvider.getRefreshTokenValidityInMs();


        try {
            stringRedisTemplate.opsForValue()
                    .set("RT:" + member.getId(), refreshToken, refreshTokenExpireMs, TimeUnit.MILLISECONDS);
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failed while saving refresh token. Block login. memberId={}", member.getId(), e);
            redirectWithError(request, response, "REDIS_DOWN");
            return;
        } catch (DataAccessException e) {
            log.error("Redis data access error while saving refresh token. Block login. memberId={}", member.getId(), e);
            redirectWithError(request, response, "REDIS_ERROR");
            return;
        }


        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpireMs / 1000)
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());


        String targetUrl = UriComponentsBuilder
                .fromUriString(frontendRedirectUrl + "/auth/oauth2/redirect")
                .queryParam("accessToken", accessToken)
                .queryParam("memberId", member.getId())
                .queryParam("memberName", member.getMemberName())
                .queryParam("memberEmail", member.getMemberEmail())
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUriString();

        log.info("targetUrl: {}", targetUrl);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }


    private boolean isRedisAvailable() {
        try {
            stringRedisTemplate.hasKey("health:redis"); // exists 체크는 가볍고, 연결 실패 시 예외 발생
            return true;
        } catch (RedisConnectionFailureException e) {
            return false;
        } catch (DataAccessException e) {
            return false;
        }
    }


    private void redirectWithError(HttpServletRequest request,
                                   HttpServletResponse response,
                                   String errorCode) throws IOException {

        String failUrl = UriComponentsBuilder
                .fromUriString(frontendRedirectUrl + "/auth/oauth2/redirect")
                .queryParam("error", errorCode)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, failUrl);
    }
}
